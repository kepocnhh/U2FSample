package test.android.u2f.provider

import org.json.JSONObject
import test.android.u2f.entity.AuthenticatorAttestationResponse
import test.android.u2f.entity.CollectedClientData
import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialCreationOptions
import kotlin.io.encoding.Base64

internal class FinalU2FProvider(
    private val sha256: Hashes,
) : U2FProvider {
    override fun create(options: PublicKeyCredentialCreationOptions): PublicKeyCredential {
        val rawId = sha256.map(options.rp.id.toByteArray() + options.user.id)
        val ccd = CollectedClientData(
            type = "webauthn.create",
            challenge = Base64.encode(options.challenge),
            origin = options.rp.id,
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
