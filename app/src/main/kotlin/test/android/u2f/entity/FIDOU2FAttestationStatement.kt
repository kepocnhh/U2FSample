package test.android.u2f.entity

/**
 * https://w3c.github.io/webauthn/#sctn-fido-u2f-attestation
 */
internal class FIDOU2FAttestationStatement(
    attestnCert: ByteArray,

    /**
     * The attestation signature.
     * The signature was calculated over the (raw) U2F registration response message
     * [FIDO-U2F-Message-Formats](https://w3c.github.io/webauthn/#biblio-fido-u2f-message-formats)
     * received by the client from the authenticator.
     */
    val sig: ByteArray,
) {
    /**
     * A single element array containing the attestation certificate in X.509 format.
     */
    val x5c = arrayOf(attestnCert)
}
