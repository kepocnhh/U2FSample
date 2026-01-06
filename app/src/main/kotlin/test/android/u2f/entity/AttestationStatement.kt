package test.android.u2f.entity

/**
 * https://fidoalliance.org/specs/fido-v2.0-ps-20150904/fido-key-attestation-v2.0-ps-20150904.html#attestation-statement
 */
internal class AttestationStatement(
    /**
     * the header object, containing the signing algorithm and additional information
     * required to verify the attestation signature.
     */
    val header: AttestationHeader,

    /**
     * the core object, containing the attested data.
     * This object is a container and can carry multiple, authenticator model specific,
     * attestation rawData types (see section 3.4 Attestation Raw Data Types).
     */
    val core: AttestationCore,

    /**
     * the signature object.
     * This object contains the cryptographic signature computed over the rawData object.
     * The structure of this object depends on the signature algorithm.
     */
    val signature: ByteArray,
)
