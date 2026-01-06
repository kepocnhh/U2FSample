package test.android.u2f.provider

import java.security.KeyPair

internal interface Secrets {
    fun newKeyPair(algorithm: String, keySize: Int): KeyPair
}
