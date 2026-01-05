package test.android.u2f.provider

internal interface U2FProvider {
    fun process(challenge: ByteArray)
}
