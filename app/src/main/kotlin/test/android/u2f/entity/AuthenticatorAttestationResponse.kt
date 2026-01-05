package test.android.u2f.entity

/**
 * https://w3c.github.io/webauthn/#authenticatorattestationresponse
 */
internal class AuthenticatorAttestationResponse(
    /**
     * This attribute contains a JSON-compatible serialization of the client data,
     * the hash of which is passed to the authenticator by the client in its call
     * to either create() or get() (i.e., the client data itself is not sent to the authenticator).
     */
    val clientDataJSON: ByteArray,

    // [fmt][attStmt][authData]
    // [attStmt] = ?
    // [authData] = [rp id hash][flags][counter] + [attested cred data]? + [extensions]?
    // [attested cred data] = [aaguid][l][credential id][credential public key]

    /**
     * This attribute contains an attestation object, which is opaque to,
     * and cryptographically protected against tampering by, the client.
     * The attestation object contains both authenticator data and an attestation statement.
     * The former contains the AAGUID, a unique credential ID, and the credential public key.
     * The contents of the attestation statement are determined by the attestation statement format
     * used by the authenticator. It also contains any additional information
     * that the Relying Party’s server requires to validate the attestation statement,
     * as well as to decode and validate the authenticator data along
     * with the JSON-compatible serialization of client data. For more details,
     * see [§6.5 Attestation](https://w3c.github.io/webauthn/#sctn-attestation),
     * [§6.5.4 Generating an Attestation Object](https://w3c.github.io/webauthn/#sctn-generating-an-attestation-object),
     * and [Figure 6](https://w3c.github.io/webauthn/#fig-attStructs).
     */
    val attestationObject: ByteArray,
)
