package com.github.christophpickl.myblockchain.client

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.http4k.Response4k
import com.github.christophpickl.kpotpourri.http4k.SC_200_Ok
import com.github.christophpickl.kpotpourri.http4k.SC_202_Accepted
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.kpotpourri.http4k.post
import com.github.christophpickl.kpotpourri.http4k.put
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.github.christophpickl.myblockchain.common.SignatureUtils
import com.github.christophpickl.myblockchain.common.encodeBase64
import com.github.christophpickl.myblockchain.common.toBytes
import com.github.christophpickl.myblockchain.common.toPrettyString
import com.github.christophpickl.myblockchain.server.Address
import com.github.christophpickl.myblockchain.server.Block
import com.github.christophpickl.myblockchain.server.Node
import com.github.christophpickl.myblockchain.server.Transaction
import java.io.File

val keyPriv = File("key.priv")
val keyPub = File("key.pub")

fun main(args: Array<String>) {
    Logback4k.reconfigure {
        addConsoleAppender {
            rootLevel = Level.WARN
            packageLevel(Level.ALL,
                    "com.github.christophpickl.myblockchain",
                    "com.github.christophpickl.kpotpourri.http4k"
            )
        }
    }
    val client = BlockchainClient()
    if (true) {
//        val tx = client.getTransactions()[0]
//        println(tx)
//        client.getBlockchain().prettyPrint()
//        client.stopMiner()
        return
    }
//    CryptService().createKeys()

    val pubBytes = keyPub.toBytes()
    val privBytes = keyPriv.toBytes()
    println("Pub hash: ${pubBytes.encodeBase64()}")
    val address = Address("myName", pubBytes)
    println("Address hash: ${address.hash.toPrettyString()}")
    client.addAddress(address)

    val sender = address.hash
    val text = "myTxText"
    val signature = SignatureUtils.sign(text.toByteArray(), privBytes)
    client.addTransaction(Transaction(text, sender, signature))

}

class BlockchainClient(
//        serverBaseUrl: UrlConfig = UrlConfig(
//                protocol = HttpProtocol.Http,
//                hostName = "localhost",
//                port = 8080
//        )
        serverBaseUrl: String = "http://localhost:8080"
) {

    private val http4k = buildHttp4k {
        baseUrlBy(serverBaseUrl)
        enforceStatusCheck(SC_200_Ok)
    }

    fun getAddresses(): List<Address> = http4k.get("/address")

    fun addAddress(address: Address, publish: Boolean = true) {
        http4k.put<Response4k>("/address") {
            enforceStatusCheck(SC_202_Accepted)
            requestBody(address)
            queryParams += "publish" to publish.toString()
        }
    }

    fun getTransactions(): List<Transaction> = http4k.get("/transaction")

    fun addTransaction(transaction: Transaction, publish: Boolean = true) {
        http4k.put<Response4k>("/transaction") {
            enforceStatusCheck(SC_202_Accepted)
            requestBody(transaction)
            queryParams += "publish" to publish.toString()
        }
    }

    fun getBlockchain(): List<Block> = http4k.get("/block")

    fun addBlock(block: Block, publish: Boolean = true) {
        http4k.put<Response4k>("/address") {
            requestBody(block)
            queryParams += "publish" to publish.toString()
        }
    }

    fun startMiner() {
        http4k.get<Any>("/block/start-miner")
    }

    fun stopMiner() {
        http4k.get<Any>("/block/stop-miner")
    }

    fun getNodes(): List<Node> = http4k.get("/node")

    fun getIp(): String = http4k.get("/node/ip")

    fun addNode(node: Node) {
        http4k.put<Any>("/node") {
            requestBody(node)
        }
    }

    fun removeNode(node: Node) {
        http4k.post<Any>("/node/remove") {
            requestBody(node)
        }
    }

}


