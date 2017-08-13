package com.github.christophpickl.myblockchain.common

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec


object SignatureUtils {

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
