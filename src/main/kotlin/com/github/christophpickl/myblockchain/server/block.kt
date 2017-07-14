package com.github.christophpickl.myblockchain.server

import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.google.common.primitives.Longs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletResponse

class Block(
        val previousBlockHash: ByteArray?,
        val transactions: List<Transaction>,
        val tries: Long

) {
    val timestamp = System.currentTimeMillis()
    val merkleRoot = calculateMerkleRoot()
    val hash = calculateBlockHash()

    fun calculateMerkleRoot(): ByteArray {
        val queue = transactions.map { it.hash }.toMutableList()
        while (queue.size > 1) {
            queue += calculateHash(queue.removeAt(0), queue.removeAt(0))
        }
        return queue[0]
    }

    fun calculateBlockHash() =
            calculateHash(previousBlockHash ?: ByteArray(0), merkleRoot, Longs.toByteArray(tries), Longs.toByteArray(timestamp))

    fun getLeadingZerosCount(): Int {
        for (i in 0..hash.size - 1) {
            if (hash[i].toInt() != 0) {
                return i
            }
        }
        return hash.size
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Block) return false
        return Arrays.equals(hash, other.hash)
    }

    override fun hashCode() = Arrays.hashCode(hash)
}

@Service
class BlockService @Autowired constructor(
        private val transactionService: TransactionService
) {

    private val http4k = buildHttp4k {  }
    private val blockchain = ArrayList<Block>()

    val lastBlock get() = blockchain.lastOrNull()

    fun all(): List<Block> = blockchain

    // synchronized
    fun append(block: Block): Boolean {
        if (!block.verify()) {
            return false
        }

        blockchain.add(block)
        block.transactions.forEach { transactionService.remove(it) }
        return true
    }

    fun synchronize(node: Node) {
        blockchain += http4k.get<List<Block>>(node.address.toString() + "/block")
    }

    private fun Block.verify(): Boolean {
        if (blockchain.size > 0) {
            val lastBlockInChainHash = blockchain.last().hash
            if (!Arrays.equals(previousBlockHash, lastBlockInChainHash)) {
                return false
            }
        } else {
            if (previousBlockHash != null) {
                return false
            }
        }

        if (!Arrays.equals(merkleRoot, calculateMerkleRoot())) {
            return false
        }
        if (!Arrays.equals(hash, calculateBlockHash())) {
            return false
        }
        if (transactions.size > MAX_TRANSACTIONS_PER_BLOCK) {
            return false
        }
        if (!transactionService.containsAll(transactions)) {
            return false
        }
        if (getLeadingZerosCount() < DIFFICULTY) {
            return false
        }
        return true
    }
}


@RestController
@RequestMapping("block")
class BlockController @Autowired constructor(
        private val blockService: BlockService,
        private val nodeService: NodeService,
        private val miningService: MiningService
) {

    @RequestMapping
    fun getBlockchain() = blockService.all()

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    fun addBlock(
            @RequestBody block: Block,
            @RequestParam(required = false, defaultValue = "false")
            publish: Boolean,
            response: HttpServletResponse
    ) {
        val success = blockService.append(block)

        if (success) {
            if (publish) {
                nodeService.broadcastPut("block", block)
            }
            response.status = HttpServletResponse.SC_ACCEPTED
        } else {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        }
    }

    @RequestMapping(path = arrayOf("start-miner"))
    fun startMiner() {
        miningService.startMiner()
    }

    @RequestMapping(path = arrayOf("stop-miner"))
    fun stopMiner() {
        miningService.stopMiner()
    }
}
