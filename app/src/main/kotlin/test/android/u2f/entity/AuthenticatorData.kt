package test.android.u2f.entity

/**
 * https://w3c.github.io/webauthn/#sctn-authenticator-data
 */
internal class AuthenticatorData(
    /**
     * SHA-256 hash of the RP ID the credential is scoped to.
     */
    val rpIdHash: ByteArray,

    val flags: Byte,

    /**
     * Signature counter, 32-bit unsigned big-endian integer.
     */
    val signCount: Int,

//    val attestedCredentialData: ?,
//    val extensions: ?,
)
