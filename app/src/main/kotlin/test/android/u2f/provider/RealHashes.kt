package test.android.u2f.provider

import java.security.MessageDigest

internal class RealHashes : Hashes {
    private val digests = mutableMapOf<String, MessageDigest>()

    override fun map(algorithm: String, bytes: ByteArray): ByteArray {
        return digests.getOrPut(algorithm) {
            MessageDigest.getInstance(algorithm)
        }.digest(bytes)
    }
}
