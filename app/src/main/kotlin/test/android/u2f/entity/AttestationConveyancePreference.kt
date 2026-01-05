package test.android.u2f.entity

/**
 * https://w3c.github.io/webauthn/#enum-attestation-convey
 */
internal enum class AttestationConveyancePreference {
    None,
    Indirect,
    Direct,
    Enterprise,
}
