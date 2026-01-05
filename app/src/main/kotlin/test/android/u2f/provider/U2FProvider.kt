package test.android.u2f.provider

import test.android.u2f.entity.PublicKeyCredential
import test.android.u2f.entity.PublicKeyCredentialCreationOptions

internal interface U2FProvider {
    fun create(options: PublicKeyCredentialCreationOptions): PublicKeyCredential
}
