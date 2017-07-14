package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.alert
import tornadofx.runLater
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


class CryptService {

    private val log = LOG {}

    fun createKeys() {
        log.info("createKeys()")
        val keyPair = SignatureUtils.generateKeyPair()
        // TODO save dialog
        val keyPrivate = Paths.get("key.priv")
        val keyPublic = Paths.get("key.pub")
        Files.write(keyPrivate, keyPair.private.encoded)
        Files.write(keyPublic, keyPair.public.encoded)
        runLater {
            alert(
                    type = INFORMATION,
                    header = "Keys generated successfully",
                    content = "Saved keys to:\n" +
                            // parent == null :(
                            "${keyPrivate.toFile().absolutePath}\n" +
                            "${keyPublic.toFile().absolutePath}"
            )
        }
    }
}

private object SignatureUtils {
    private val keyFactory = KeyFactory.getInstance("DSA", "SUN")
    private val signatureAlgorithm = Signature.getInstance("SHA1withDSA", "SUN")

    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("DSA", "SUN")
        val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
        keyGen.initialize(1024, random)
        return keyGen.generateKeyPair()
    }

    fun verify(data: ByteArray, signature: ByteArray, publicKey: ByteArray): Boolean {
        val keySpec = X509EncodedKeySpec(publicKey)
        val publicKeyObj = keyFactory!!.generatePublic(keySpec)

        signatureAlgorithm.initVerify(publicKeyObj)
        signatureAlgorithm.update(data)
        return signatureAlgorithm.verify(signature)
    }

    fun sign(data: ByteArray, privateKey: ByteArray): ByteArray {
        val keySpec = PKCS8EncodedKeySpec(privateKey)
        val privateKeyObj = keyFactory!!.generatePrivate(keySpec)

        signatureAlgorithm.initSign(privateKeyObj)
        signatureAlgorithm.update(data)
        return signatureAlgorithm.sign()
    }

}
