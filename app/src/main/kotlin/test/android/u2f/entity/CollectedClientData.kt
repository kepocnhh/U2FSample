package test.android.u2f.entity

/**
 * https://w3c.github.io/webauthn/#dictdef-collectedclientdata
 */
internal class CollectedClientData(
    /**
     * This member contains the string "webauthn.create" when creating new credentials,
     * and "webauthn.get" when getting an assertion from an existing credential.
     * The purpose of this member is to prevent certain types of signature confusion attacks
     * (where an attacker substitutes one legitimate signature for another).
     */
    val type: String,
    val challenge: ByteArray,
    val origin: String,
)
