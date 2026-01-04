package test.android.u2f.provider

import test.android.u2f.entity.StartRegistrationResponse

internal interface U2FRemotes {
    fun startRegistration(): StartRegistrationResponse
}
