package test.android.u2f.entity

import java.util.UUID

internal class StartRegistrationResponse(
    val options: PublicKeyCredentialCreationOptions,
    val requestId: UUID,
)
