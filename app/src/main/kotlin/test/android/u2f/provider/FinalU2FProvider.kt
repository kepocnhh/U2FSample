package test.android.u2f.provider

import org.json.JSONObject
import test.android.u2f.entity.AuthenticatorAttestationResponse
import test.android.u2f.entity.AuthenticatorData
import test.android.u2f.entity.CollectedClientData
import test.android.u2f.entity.FIDOU2FAttestationStatement
import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialCreationOptions
import kotlin.io.encoding.Base64

internal class FinalU2FProvider(
    private val sha256: Hashes,
) : U2FProvider {
    override fun create(options: PublicKeyCredentialCreationOptions): PublicKeyCredential {
        val rawId = sha256.map(options.rp.id.toByteArray() + options.user.id) // todo
        val ccd = CollectedClientData(
            type = "webauthn.create",
            challenge = Base64.encode(options.challenge),
            origin = options.rp.id,
        )
        val authenticatorData = AuthenticatorData(
            rpIdHash = sha256.map(options.rp.id.toByteArray()),
            flags = 0b00000000,
            signCount = 1, // todo
        )
        val attestationStatement = FIDOU2FAttestationStatement(
            attestnCert = TODO("FinalU2FProvider:attestnCert"),
            sig = TODO("FinalU2FProvider:sig"),
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
