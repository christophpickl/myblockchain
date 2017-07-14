package com.github.christophpickl.myblockchain.client

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.kpotpourri.http4k.Http4k
import com.github.christophpickl.kpotpourri.http4k.Response4k
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.kpotpourri.http4k.post
import com.github.christophpickl.kpotpourri.http4k.put
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.github.christophpickl.myblockchain.server.Address
import com.github.christophpickl.myblockchain.server.Block
import com.github.christophpickl.myblockchain.server.Node
import com.github.christophpickl.myblockchain.server.Transaction

private val http4k: Http4k by lazy {
    buildHttp4k {
        baseUrlBy("http://localhost:8080")
    }
}

fun main(args: Array<String>) {
    Logback4k.reconfigure {
        addConsoleAppender {
            rootLevel = Level.WARN
            packageLevel(Level.ALL, "com.github.christophpickl.myblockchain")
            packageLevel(Level.ALL, "com.github.christophpickl.kpotpourri.http4k")
        }
    }
//    addAddress(Address("myName", Files.readAllBytes(Paths.get("key.pub"))))
//    listAddresses()

//    val text = "my text"
//    val senderHash = ByteArray(0) // FIXME implement me
//    val signature = ByteArray(0)
//    addTransaction(Transaction(text, senderHash, signature))
    listTransactions()

//    addBlock(Block(null, ...)) ... only done by miners!
    listBlockchain()

//    addNode(node)
    listNodes()

}

fun listAddresses() {
    http4k.get<List<Address>>("/address").prettyPrint()
}
fun addAddress(address: Address) {
    http4k.put<Response4k>("/address") {
        requestBody(address)
        queryParams += "publish" to "true"
    }
}

fun listTransactions() {
    http4k.get<List<Transaction>>("/transaction").prettyPrint()
}
fun addTransaction(transaction: Transaction) {
    http4k.put<Response4k>("/transaction") {
        requestBody(transaction)
        queryParams += "publish" to "true"
    }
}


fun listBlockchain() {
    http4k.get<List<Block>>("/block").prettyPrint()
}
fun addBlock(block: Block) {
    http4k.put<Response4k>("/address") {
        requestBody(block)
        queryParams += "publish" to "true"
    }
}

fun startMiner() {
    http4k.get<Any>("/block/start-miner")
}
fun stopMiner() {
    http4k.get<Any>("/block/stop-miner")
}

fun listNodes() {
    http4k.get<List<Node>>("/node").prettyPrint()
}
fun getIp() {
    println(http4k.get<String>("/node/ip"))
}
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
