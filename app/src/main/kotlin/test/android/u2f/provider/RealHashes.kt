package test.android.u2f.provider

import java.security.MessageDigest

internal class RealHashes(algorithm: String) : Hashes {
    private val md = MessageDigest.getInstance(algorithm)
    override val size = md.digestLength

    override fun map(bytes: ByteArray): ByteArray {
        return md.digest(bytes)
    }
}
