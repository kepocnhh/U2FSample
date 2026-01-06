package test.android.u2f.entity

/**
 * https://www.w3.org/TR/webauthn-3/#sctn-packed-attestation
 */
internal class PackedAttestationStatement(
    attestnCert: ByteArray,

    /**
     * https://www.w3.org/TR/webauthn-3/#sctn-alg-identifier
     * https://www.iana.org/assignments/cose/cose.xhtml#algorithms
     */
    val alg: Long,

    /**
     * A byte string containing the [attestation signature](https://www.w3.org/TR/webauthn-3/#attestation-signature).
     */
    val sig: ByteArray,
) {
    /**
     * The elements of this array contain [attestnCert] and its certificate chain (if any),
     * each encoded in X.509 format.
     * The attestation certificate [attestnCert] MUST be the first element in the array.
     */
    val x5c = arrayOf(attestnCert)
}
