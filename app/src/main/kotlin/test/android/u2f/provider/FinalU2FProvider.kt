package test.android.u2f.provider

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.ECNamedCurveTable
import org.bouncycastle.jce.ECPointUtil
import org.bouncycastle.jce.spec.ECNamedCurveSpec
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.json.JSONObject
import sp.kx.bytes.hex
import test.android.u2f.entity.AttestationCore
import test.android.u2f.entity.AttestationHeader
import test.android.u2f.entity.AttestationStatement
import test.android.u2f.entity.AuthenticatorAttestationResponse
import test.android.u2f.entity.AuthenticatorData
import test.android.u2f.entity.CollectedClientData
import test.android.u2f.entity.FIDOU2FAttestationStatement
import test.android.u2f.entity.PackedAttestationStatement
import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialCreationOptions
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.math.BigInteger
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.ECPrivateKeySpec
import java.security.spec.ECPublicKeySpec
import java.util.Date
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

internal class FinalU2FProvider(loggers: Loggers) : U2FProvider {
    private val logger = loggers.create("[U2FProvider]")
    private val sha256 = MessageDigest.getInstance("sha256")

    private fun OutputStream.write8bit(value: Int) {
        write(value)
    }

    private fun OutputStream.write16bit(value: Int) {
        write(value.shr(8))
        write(value)
    }

    private fun OutputStream.write32bit(value: Int) {
        write(value.shr(24))
        write(value.shr(16))
        write(value.shr(8))
        write(value)
    }

    private fun encoded(authenticatorData: AuthenticatorData): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            stream.write(authenticatorData.rpIdHash)
            stream.write(authenticatorData.flags.toInt())
            stream.write32bit(authenticatorData.signCount)
            // todo attestedCredentialData
            // todo extensions
            stream.toByteArray()
        }
    }

    private fun newCertificate(publicKey: PublicKey, privateKey: PrivateKey): Certificate {
        val issuer = "issuer" // todo
        val now = System.currentTimeMillis().milliseconds // todo
        val builder = JcaX509v3CertificateBuilder(
            X500Name("CN=$issuer"),
            BigInteger.valueOf(now.inWholeMilliseconds),
            Date(now.inWholeMilliseconds),
            Date((now + 3650.days).inWholeMilliseconds),
            X500Name("CN=$issuer"),
            publicKey,
        )
        val signer = JcaContentSignerBuilder("SHA256WITHECDSA").build(privateKey)
        return JcaX509CertificateConverter().getCertificate(builder.build(signer))
    }

    private fun toPrivateKey(encoded: ByteArray): PrivateKey {
        val kf = KeyFactory.getInstance("ec")
        val s = BigInteger(1, encoded)
        val spec = ECNamedCurveTable.getParameterSpec("secp256r1")
        val params = ECNamedCurveSpec(spec.name, spec.curve, spec.g, spec.n)
        val keySpec = ECPrivateKeySpec(s, params)
        return kf.generatePrivate(keySpec)
    }

    private fun toPublicKey(encoded: ByteArray): PublicKey {
        val kf = KeyFactory.getInstance("ec")
        val spec = ECNamedCurveTable.getParameterSpec("secp256r1")
        val params = ECNamedCurveSpec(spec.name, spec.curve, spec.g, spec.n)
        val w = ECPointUtil.decodePoint(params.curve, encoded)
        val keySpec = ECPublicKeySpec(w, params)
        return kf.generatePublic(keySpec)
    }

    private fun toCertificate(encoded: ByteArray): Certificate {
        val cf = CertificateFactory.getInstance("x509")
        return cf.generateCertificate(encoded.inputStream())
    }

    private fun registrationRequestMessage(
        clientDataJson: ByteArray,
        origin: String,
    ): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            stream.write(sha256.digest(clientDataJson))
            stream.write(sha256.digest(origin.toByteArray()))
            stream.toByteArray()
        }
    }

    private fun registrationResponseMessage(
        publicKey: PublicKey,
        keyHandle: ByteArray,
        attestationCrt: Certificate,
        signature: ByteArray,
    ): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            stream.write(0x05)
            stream.write(publicKey.encoded)
            stream.write8bit(keyHandle.size)
            stream.write(keyHandle)
            stream.write(attestationCrt.encoded)
            stream.write(signature)
            stream.toByteArray()
        }
    }

    private fun signee(
        applicationParam: ByteArray,
        challengeParam: ByteArray,
        keyHandle: ByteArray,
        publicKey: PublicKey,
    ): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            stream.write(0x00)
            stream.write(applicationParam)
            stream.write(challengeParam)
            stream.write(keyHandle)
            stream.write(publicKey.encoded)
            stream.toByteArray()
        }
    }

    private fun sign(privateKey: PrivateKey, signee: ByteArray): ByteArray {
        val sig = Signature.getInstance("ECDSA")
        sig.initSign(privateKey)
        sig.update(signee)
        return sig.sign()
    }

    override fun create(options: PublicKeyCredentialCreationOptions): PublicKeyCredential {
        val rawId = sha256.digest(options.rp.id.toByteArray() + options.user.id) // todo
        val ccd = CollectedClientData(
            type = "webauthn.create",
            challenge = Base64.encode(options.challenge),
            origin = options.rp.id,
        )
        val authenticatorData = AuthenticatorData(
            rpIdHash = sha256.digest(options.rp.id.toByteArray()),
            flags = 0b00000000,
            signCount = 1, // todo
        )
//        val keyPair = secrets.newKeyPair(algorithm = "EC", keySize = 256)
//        val privateKey = keyPair.private
//        val publicKey = keyPair.public
//        val crt = newCertificate(publicKey = publicKey, privateKey = privateKey)
        //
        val attestationPrivateKeyEncoded = "f3fccc0d00d8031954f90864d43c247f4bf5f0665c6b50cc17749a27d1cf7664".hexToByteArray()
        logger.debug("attestation:private:key: ${attestationPrivateKeyEncoded.hex()}")
        val attestationPrivateKey = toPrivateKey(attestationPrivateKeyEncoded)
        logger.debug("attestation:private:key:sha256: ${sha256.digest(attestationPrivateKey.encoded).hex()}")
        //
        val attestationPublicKeyEncoded = "048d617e65c9508e64bcc5673ac82a6799da3c1446682c258c463fffdf58dfd2fa3e6c378b53d795c4a4dffb4199edd7862f23abaf0203b4b8911ba0569994e101".hexToByteArray()
        logger.debug("attestation:public:key: ${attestationPublicKeyEncoded.hex()}")
        val attestationPublicKey = toPublicKey(attestationPublicKeyEncoded)
        logger.debug("attestation:public:key:sha256: ${sha256.digest(attestationPublicKey.encoded).hex()}")
        //
        val attestationCrtEncoded = "3082013c3081e4a003020102020a47901280001155957352300a06082a8648ce3d0403023017311530130603550403130c476e756262792050696c6f74301e170d3132303831343138323933325a170d3133303831343138323933325a3031312f302d0603550403132650696c6f74476e756262792d302e342e312d34373930313238303030313135353935373335323059301306072a8648ce3d020106082a8648ce3d030107034200048d617e65c9508e64bcc5673ac82a6799da3c1446682c258c463fffdf58dfd2fa3e6c378b53d795c4a4dffb4199edd7862f23abaf0203b4b8911ba0569994e101300a06082a8648ce3d0403020347003044022060cdb6061e9c22262d1aac1d96d8c70829b2366531dda268832cb836bcd30dfa0220631b1459f09e6330055722c8d89b7f48883b9089b88d60d1d9795902b30410df".hexToByteArray()
        logger.debug("attestation:crt: ${attestationCrtEncoded.hex()}")
        val attestationCrt = toCertificate(attestationCrtEncoded)
        logger.debug("attestation:crt:sha256: ${sha256.digest(attestationCrt.encoded).hex()}")
        check(attestationCrt is X509Certificate)
        logger.debug("attestation:crt:subject: ${attestationCrt.subjectX500Principal.name}")
        logger.debug("attestation:crt:expires: ${attestationCrt.notAfter}")
        //
        val privateKey = toPrivateKey("9a9684b127c5e3a706d618c86401c7cf6fd827fd0bc18d24b0eb842e36d16df1".hexToByteArray())
        logger.debug("private:key:sha256: ${sha256.digest(privateKey.encoded).hex()}")
        //
        val publicKey = toPublicKey("04b174bc49c7ca254b70d2e5c207cee9cf174820ebd77ea3c65508c26da51b657c1cc6b952f8621697936482da0a6d3d3826a59095daf6cd7c03e2e60385d2f6d9".hexToByteArray())
        logger.debug("public:key:sha256: ${sha256.digest(publicKey.encoded).hex()}")
        //
        val keyHandle = "2a552dfdb7477ed65fd84133f86196010b2215b57da75d315b7b9e8fe2e3925a6019551bab61d16591659cbaf00b4950f7abfe6660e2e006f76868b772d70c25".hexToByteArray()
        logger.debug("key:handle: ${keyHandle.hex()}")
        //
        val clientDataJson = JSONObject()
            .put("type", ccd.type)
            .put("challenge", ccd.challenge)
            .put("origin", ccd.origin)
            .toString()
            .toByteArray()
        val clientDataHash = sha256.digest(clientDataJson)
        val authenticatorDataEncoded = encoded(authenticatorData = authenticatorData)
        val applicationParam = sha256.digest(ccd.origin.toByteArray())
        val signee = signee(
            applicationParam = applicationParam,
            challengeParam = clientDataHash,
            keyHandle = keyHandle,
            publicKey = publicKey,
        )
        val signature = sign(privateKey = privateKey, signee = signee)
        logger.debug("signature:sha256: ${sha256.digest(signature).hex()}")
        val regResMessage = registrationResponseMessage(
            publicKey = publicKey,
            keyHandle = keyHandle,
            attestationCrt = attestationCrt,
            signature = signature,
        )
        val attestationStatement = FIDOU2FAttestationStatement(
            attestnCert = attestationCrt.encoded,
            sig = signature,
        )
        val response = AuthenticatorAttestationResponse(
            clientDataJSON = clientDataJson,
            attestationObject = TODO("FinalU2FProvider:response:attestationObject"),
        )
        return PublicKeyCredential(
            rawId = rawId,
            response = response,
        )
    }
}
