package test.android.u2f.provider

import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate

internal interface Secrets {
    fun newKeyPair(algorithm: String, keySize: Int): KeyPair
    fun newCertificate(publicKey: PublicKey, privateKey: PrivateKey): Certificate
}
