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
        val masterNode = Node(URL(MASTER_NODE_ADDRESS))
        val host = http4k.get<String>(masterNode.address.toString() + "/node/ip")
        val port = event.embeddedServletContainer.port
        self = Node(URL("http", host, port, ""))

        if (self == masterNode) {
            log.info { "Running as master node on: $self" }
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
        broadcastPost("node/remove", self)
    }

    fun all(): Set<Node> = knownNodes

    // synchronized
    fun add(node: Node) {
        knownNodes.add(node)
    }

    // synchronized
    fun remove(node: Node) {
        knownNodes.remove(node)
    }

    // TODO type safe endpoint
    fun broadcastPut(endpoint: String, data: Any) {
        knownNodes.parallelStream().forEach { (address) ->
            http4k.put(address.toString() + "/" + endpoint) {
                requestBody(data)
            }
        }
    }
    fun broadcastPost(endpoint: String, data: Any) {
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

    @RequestMapping
    fun getNodes() = nodeService.all()

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun addNode(@RequestBody node: Node) {
        nodeService.add(node)
    }

    @RequestMapping(path = arrayOf("remove"), method = arrayOf(RequestMethod.POST))
    internal fun removeNode(@RequestBody node: Node) {
        nodeService.remove(node)
    }

    @RequestMapping(path = arrayOf("ip"))
    fun getIp(request: HttpServletRequest): String {
        return request.remoteAddr
    }

}
