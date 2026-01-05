package test.android.u2f.entity

import kotlin.time.Duration

/**
 * https://w3c.github.io/webauthn/#dictdef-publickeycredentialcreationoptions
 */
internal class PublicKeyCredentialCreationOptions(
    val rp: PublicKeyCredentialRpEntity,
    val user: PublicKeyCredentialUserEntity,
    val challenge: ByteArray,
    val timeout: Duration,
    val attestation: AttestationConveyancePreference,
)
