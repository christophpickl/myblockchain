package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.http4k.Response4k
import com.github.christophpickl.kpotpourri.http4k.SC_200_Ok
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.kpotpourri.http4k.post
import com.github.christophpickl.kpotpourri.http4k.put
import com.github.christophpickl.myblockchain.server.Address
import com.github.christophpickl.myblockchain.server.Block
import com.github.christophpickl.myblockchain.server.Node
import com.github.christophpickl.myblockchain.server.Transaction

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

    fun getAddresses() = http4k.get<List<Address>>("/address")

    fun addAddress(name: String, publicKey: ByteArray, publish: Boolean = true) {
        http4k.put<Response4k>("/address") {
            requestBody(Address(name, publicKey))
            queryParams += "publish" to publish.toString()
        }
    }

    fun getTransactions() = http4k.get<List<Transaction>>("/transaction")

    fun addTransaction(transaction: Transaction, publish: Boolean = true) {
        http4k.put<Response4k>("/transaction") {
            requestBody(transaction)
            queryParams += "publish" to publish.toString()
        }
    }

    fun getBlockchain() = http4k.get<List<Block>>("/block")

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

    fun getNodes() = http4k.get<List<Node>>("/node")

    fun getIp() = http4k.get<String>("/node/ip")

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

// fun File.toBytes() = Files.readAllBytes(toPath()))
