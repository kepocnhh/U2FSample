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

    /**
     * This member contains the base64url encoding of the challenge provided by the Relying Party.
     * See the [§13.4.3 Cryptographic Challenges](https://w3c.github.io/webauthn/#sctn-cryptographic-challenges)
     * security consideration.
     */
    val challenge: String,

    /**
     * This member contains the fully qualified origin of the requester,
     * as provided to the authenticator by the client, in the syntax defined by
     * [RFC6454](https://w3c.github.io/webauthn/#biblio-rfc6454).
     */
    val origin: String,
)
