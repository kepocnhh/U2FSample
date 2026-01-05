package test.android.u2f.entity

/**
 * https://w3c.github.io/webauthn/#dictdef-publickeycredentialuserentity
 */
internal class PublicKeyCredentialUserEntity(
    val id: ByteArray,
    val name: String,
    val displayName: String,
)
