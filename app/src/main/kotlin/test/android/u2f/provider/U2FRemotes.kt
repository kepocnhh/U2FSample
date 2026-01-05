package test.android.u2f.provider

import test.android.u2f.entity.PublicKeyCredentialCreationOptions

internal interface U2FRemotes {
    fun startRegistration(): PublicKeyCredentialCreationOptions
}
