package test.android.u2f.provider

internal interface Hashes {
    fun map(algorithm: String, bytes: ByteArray): ByteArray
}
