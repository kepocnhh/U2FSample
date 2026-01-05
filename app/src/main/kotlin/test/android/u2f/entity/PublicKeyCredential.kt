package test.android.u2f.entity

import kotlin.io.encoding.Base64

/**
 * https://w3c.github.io/webauthn/#publickeycredential
 */
internal class PublicKeyCredential(
    val rawId: ByteArray,
    val response: AuthenticatorAttestationResponse,
) {
    val id = Base64.encode(rawId)
    val type = "public-key"
}
