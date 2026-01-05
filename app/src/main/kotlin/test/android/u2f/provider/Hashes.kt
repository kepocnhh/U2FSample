package test.android.u2f.provider

internal interface Hashes {
    val size: Int
    fun map(bytes: ByteArray): ByteArray
}
