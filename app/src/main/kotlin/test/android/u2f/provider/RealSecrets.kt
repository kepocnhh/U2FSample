package test.android.u2f.provider

import java.security.KeyPair
import java.security.KeyPairGenerator

internal class RealSecrets : Secrets {
    override fun newKeyPair(algorithm: String, keySize: Int): KeyPair {
        val kpg = KeyPairGenerator.getInstance(algorithm)
        kpg.initialize(keySize)
        return kpg.generateKeyPair()
    }
}
