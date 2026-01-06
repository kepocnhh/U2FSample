package test.android.u2f.entity

/**
 * https://fidoalliance.org/specs/fido-v2.0-ps-20150904/fido-key-attestation-v2.0-ps-20150904.html#idl-def-AttestationHeader
 */
internal class AttestationHeader(
    /**
     * Attestation Certificate and its certificate chain
     * as described in [JWS](https://datatracker.ietf.org/doc/html/rfc7515) section 4.1.6.
     */
    val x5c: Array<ByteArray>,

    /**
     * The name of the algorithm used to generate the attestation signature according to [JWA](https://datatracker.ietf.org/doc/html/rfc7518).
     * See section for the signature algorithms to be implemented by FIDO Servers.
     */
    val alg: Long,
)
