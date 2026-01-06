package test.android.u2f.entity

/**
 * https://fidoalliance.org/specs/fido-v2.0-ps-20150904/fido-key-attestation-v2.0-ps-20150904.html#idl-def-AttestationCore
 */
internal class AttestationCore(
    /**
     * The type of the rawData object.
     * This specification defines these attestation types: "tpm", "packed", and "android".
     * Other attestation types may be defined in further versions of this specification.
     */
    val type: String,

    /**
     * The version number of the rawData object.
     */
    val version: Long,

    /**
     * The rawData object (for type "android"),
     * or the base64url-encoded rawData object (for types "tpm" and "packed"),
     * containing the attested public key and the clientDataHash.
     */
    val rawData: ByteArray,

    /**
     * A base64url encoding of clientDataJSON [FIDOSignatureFormat](https://fidoalliance.org/specs/fido-v2.0-ps-20150904/fido-signature-format.html).
     * The exact encoding must be preserved as the hash (clientDataHash) has been computed over it.
     */
    val clientData: ByteArray,
)
