package test.android.u2f.provider

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.Certificate
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

internal class RealSecrets : Secrets {
    override fun newKeyPair(algorithm: String, keySize: Int): KeyPair {
        val kpg = KeyPairGenerator.getInstance(algorithm)
        kpg.initialize(keySize)
        return kpg.generateKeyPair()
    }

    override fun newCertificate(publicKey: PublicKey, privateKey: PrivateKey): Certificate {
        val issuer = "issuer" // todo
        val now = System.currentTimeMillis().milliseconds // todo
        val builder = JcaX509v3CertificateBuilder(
            X500Name("CN=$issuer"),
            BigInteger.valueOf(now.inWholeMilliseconds),
            Date(now.inWholeMilliseconds),
            Date((now + 3650.days).inWholeMilliseconds),
            X500Name("CN=$issuer"),
            publicKey,
        )
        val signer = JcaContentSignerBuilder("SHA256WITHECDSA")
            .build(privateKey)
        return JcaX509CertificateConverter().getCertificate(builder.build(signer))
    }
}
