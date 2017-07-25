package com.github.christophpickl.myblockchain.server

import org.apache.commons.codec.digest.DigestUtils
import java.util.LinkedList

fun calculateHash(vararg byteArrays: ByteArray): ByteArray {
    val bytes: List<Byte> = byteArrays.toList().fold(LinkedList<Byte>(), { accumulator, byteArray ->  accumulator.apply { addAll(byteArray.toList()) }})
    return DigestUtils.sha256(bytes.toByteArray())
}


