package test.android.u2f.provider

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
import kotlin.io.encoding.Base64

internal class FinalU2FProvider(
    private val hashes: Hashes,
    private val secrets: Secrets,
    loggers: Loggers,
) : U2FProvider {
    private val logger = loggers.create("[U2FProvider]")

    private fun OutputStream.write32bit(value: Int) {
        write(value.shr(24))
        write(value.shr(16))
        write(value.shr(8))
        write(value)
    }

    private fun OutputStream.write16bit(value: Int) {
        write(value.shr(8))
        write(value)
    }

    override fun create(options: PublicKeyCredentialCreationOptions): PublicKeyCredential {
        val rawId = hashes.map("sha256", options.rp.id.toByteArray() + options.user.id) // todo
        val ccd = CollectedClientData(
            type = "webauthn.create",
            challenge = Base64.encode(options.challenge),
            origin = options.rp.id,
        )
        val authenticatorData = AuthenticatorData(
            rpIdHash = hashes.map("sha256", options.rp.id.toByteArray()),
            flags = 0b00000000,
            signCount = 1, // todo
        )
        val keyPair = secrets.newKeyPair(algorithm = "EC", keySize = 256)
        logger.debug("public:key: ${hashes.map("sha256", keyPair.public.encoded).hex()}")
        logger.debug("private:key: ${hashes.map("sha256", keyPair.private.encoded).hex()}")
        val crt = secrets.newCertificate(publicKey = keyPair.public, privateKey = keyPair.private)
        logger.debug("crt: ${hashes.map("sha256", crt.encoded).hex()}")
        val clientDataJson = JSONObject()
            .put("type", ccd.type)
            .put("challenge", ccd.challenge)
            .put("origin", ccd.origin)
            .toString()
            .toByteArray()
        val clientDataHash = hashes.map("sha256", clientDataJson)
        val rawData = ByteArrayOutputStream().use { stream ->
            // 0xF1D0, fixed big-endian TAG to make sure this object won't be confused with other (non-FIDO) binary objects.
            stream.write(0xf1)
            stream.write(0xd0)
            // Flags
            stream.write(0x00)
            // Signature counter (signCount), 32-bit unsigned big-endian integer.
            stream.write32bit(authenticatorData.signCount)
            // Public key algorithm and encoding (16-bit big-endian value). Allowed values are:
            // 0x0100. This is raw ANSI X9.62 formatted Elliptic Curve public key [SEC1].
            stream.write(0x01)
            stream.write(0x00)
            // Byte length m of following public key bytes (16 bit value with most significant byte first).
            stream.write16bit(keyPair.public.encoded.size)
            // The public key (m bytes) according to the encoding denoted before.
            stream.write(keyPair.public.encoded)
            // Byte length l of KeyHandle
            TODO()
            // KeyHandle (l bytes)
            TODO()
            // Byte length n of clientDataHash
            stream.write16bit(clientDataHash.size)
            // clientDataHash (see section 3.2.2 Client data). This is the hash of clientData. The hash algorithm itself is stored in the clientData object [FIDOSignatureFormat].
            stream.write(clientDataHash)
            //
            stream.toByteArray()
        }
        val attestationStatement = AttestationStatement(
            header = AttestationHeader(
                x5c = arrayOf(crt.encoded),
                alg = -9, // ESP256
            ),
            core = AttestationCore(
                type = "packed",
                version = 1, // todo
                rawData = rawData,
                clientData = Base64.encodeToByteArray(clientDataJson),
            ),
            signature = TODO("FinalU2FProvider:statement:signature"),
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
