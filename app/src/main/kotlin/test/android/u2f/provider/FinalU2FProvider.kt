package test.android.u2f.provider

import org.json.JSONObject
import sp.kx.bytes.hex
import test.android.u2f.entity.AuthenticatorAttestationResponse
import test.android.u2f.entity.AuthenticatorData
import test.android.u2f.entity.CollectedClientData
import test.android.u2f.entity.FIDOU2FAttestationStatement
import test.android.u2f.entity.PackedAttestationStatement
import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialCreationOptions
import kotlin.io.encoding.Base64

internal class FinalU2FProvider(
    private val hashes: Hashes,
    private val secrets: Secrets,
    loggers: Loggers,
) : U2FProvider {
    private val logger = loggers.create("[U2FProvider]")

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
        val attestationStatement = PackedAttestationStatement(
            attestnCert = TODO("FinalU2FProvider:packed:attestnCert"),
            alg = -9, // ESP256
            sig = TODO("FinalU2FProvider:packed:sig"),
        )
        val response = AuthenticatorAttestationResponse(
            clientDataJSON = JSONObject()
                .put("type", ccd.type)
                .put("challenge", ccd.challenge)
                .put("origin", ccd.origin)
                .toString()
                .toByteArray(),
            attestationObject = TODO("FinalU2FProvider:response:attestationObject"),
        )
        return PublicKeyCredential(
            rawId = rawId,
            response = response,
        )
    }
}
