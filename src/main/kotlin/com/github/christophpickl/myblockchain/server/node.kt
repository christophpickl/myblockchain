package com.github.christophpickl.myblockchain.server

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.kpotpourri.http4k.post
import com.github.christophpickl.kpotpourri.http4k.put
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.net.URL
import javax.annotation.PreDestroy
import javax.servlet.http.HttpServletRequest

val MASTER_NODE_ADDRESS = "http://localhost:8080"

data class Node(
        val address: URL
)

@Service
class NodeService @Autowired constructor(
        private val addressService: AddressService,
        private val transactionService: TransactionService,
        private val blockService: BlockService
) : ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    private val log = LOG {}
    private val http4k = buildHttp4k { }
    private val knownNodes = HashSet<Node>()
    private lateinit var self: Node

    override fun onApplicationEvent(event: EmbeddedServletContainerInitializedEvent) {
        log.info { "onApplicationEvent(event)" }

        val masterNode = Node(URL(MASTER_NODE_ADDRESS))
        val host = http4k.get<String>(masterNode.address.toString() + "/node/ip")
        val port = event.embeddedServletContainer.port
        self = Node(URL("http", host, port, ""))

        if (self == masterNode) {
            log.debug { "Running as master node on: $self" }
        } else {
            knownNodes += masterNode
            knownNodes += http4k.get<List<Node>>(masterNode.address.toString() + "/node")

            addressService.synchronize(masterNode)
            blockService.synchronize(masterNode)
            transactionService.synchronize(masterNode)

            broadcastPut("node", self)
        }
    }

    @PreDestroy
    fun shutdown() {
        log.debug { "shutdown()" }
        broadcastPost("node/remove", self)
    }

    fun all(): Set<Node> = knownNodes

    // synchronized
    fun add(node: Node) {
        log.debug { "add(node=$node)" }
        knownNodes.add(node)
    }

    // synchronized
    fun remove(node: Node) {
        log.debug { "add(node=$node)" }
        knownNodes.remove(node)
    }

    // TODO type safe endpoint
    fun broadcastPut(endpoint: String, data: Any) {
        log.debug { "broadcastPut(endpoint=$endpoint, data=$data)" }
        knownNodes.parallelStream().forEach { (address) ->
            http4k.put(address.toString() + "/" + endpoint) {
                requestBody(data)
            }
        }
    }

    fun broadcastPost(endpoint: String, data: Any) {
        log.debug { "broadcastPut(endpoint=$endpoint, data=$data)" }
        knownNodes.parallelStream().forEach { (address) ->
            http4k.post(address.toString() + "/" + endpoint) {
                requestBody(data)
            }
        }
    }

}

@RestController
@RequestMapping("node")
class NodeController @Autowired constructor(
        private val nodeService: NodeService
) {

    private val log = LOG {}

    @RequestMapping
    fun getNodes() = nodeService.all()

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun addNode(@RequestBody node: Node) {
        log.debug { "addNode(node=$node)" }
        nodeService.add(node)
    }

    @RequestMapping(path = arrayOf("remove"), method = arrayOf(RequestMethod.POST))
    internal fun removeNode(@RequestBody node: Node) {
        log.debug { "removeNode(node=$node)" }
        nodeService.remove(node)
    }

    @RequestMapping(path = arrayOf("ip"))
    fun getIp(request: HttpServletRequest): String {
        log.debug { "getIp() => ${request.remoteAddr}" }
        return request.remoteAddr
    }

}
