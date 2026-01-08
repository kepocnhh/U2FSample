package test.android.u2f.provider

import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialUserEntity
import test.android.u2f.entity.StartRegistrationResponse
import java.util.UUID

internal interface U2FRemotes {
    fun startRegistration(): StartRegistrationResponse
    fun finishRegistration(
        credential: PublicKeyCredential,
        user: PublicKeyCredentialUserEntity,
        requestId: UUID,
    )
}
