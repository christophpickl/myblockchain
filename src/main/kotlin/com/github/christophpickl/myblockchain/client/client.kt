package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.http4k.Response4k
import com.github.christophpickl.kpotpourri.http4k.SC_200_Ok
import com.github.christophpickl.kpotpourri.http4k.SC_202_Accepted
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.kpotpourri.http4k.post
import com.github.christophpickl.kpotpourri.http4k.put
import com.github.christophpickl.myblockchain.server.Address
import com.github.christophpickl.myblockchain.server.Block
import com.github.christophpickl.myblockchain.server.Node
import com.github.christophpickl.myblockchain.server.Transaction
import java.io.File

val keyPriv = File("key.priv")
val keyPub = File("key.pub")

class BlockchainClient(
        serverBaseUrl: String = "http://localhost:8080"
) {

    private val http4k = buildHttp4k {
        baseUrlBy(serverBaseUrl)
        enforceStatusCheck(SC_200_Ok)
    }

    fun executeGet(url: String) = http4k.get<Response4k>(url)

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


