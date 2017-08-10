package com.github.christophpickl.myblockchain.server

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.myblockchain.common.SignatureUtils
import com.github.christophpickl.myblockchain.common.encodeBase64
import com.github.christophpickl.myblockchain.common.toPrettyString
import com.google.common.base.MoreObjects
import com.google.common.primitives.Longs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.Arrays
import java.util.HashSet
import javax.servlet.http.HttpServletResponse


class Transaction(
        val text: String,
        val senderHash: ByteArray,
        val signature: ByteArray

) {
    val timestamp = System.currentTimeMillis()
    val hash = calculateTransactionHash()

    fun calculateTransactionHash() = calculateHash(text.toByteArray(), senderHash, signature, Longs.toByteArray(timestamp))

    override fun equals(other: Any?): Boolean {
        if (other !is Transaction) return false
        return Arrays.equals(this.hash, other.hash)
    }

    override fun hashCode() = Arrays.hashCode(hash)

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("text", text)
            .add("timestamp", timestamp)
            .add("hash", hash.toPrettyString())
            .add("senderHash", senderHash.toPrettyString())
            .add("signature", signature.toPrettyString())
            .toString()
}


@Service
class TransactionService @Autowired constructor(
        private val addressService: AddressService
) {
    private val log = LOG {}
    private val http4k = buildHttp4k {  }
    private val transactionPool = HashSet<Transaction>()

    fun all(): Set<Transaction> = transactionPool

    // synchronized
    fun add(transaction: Transaction): Boolean {
        if (transaction.verify()) {
            transactionPool += transaction
            log.debug { "add(transaction=$transaction) ... valid and added" }
            return true
        }
        log.debug { "add(transaction=$transaction) ... invalid, skipp adding" }
        return false
    }

    fun remove(transaction: Transaction) {
        log.debug { "remove(transaction=$transaction)" }
        transactionPool.remove(transaction)
    }

    fun containsAll(transactions: Collection<Transaction>): Boolean {
        return transactionPool.containsAll(transactions)
    }

    private fun Transaction.verify(): Boolean {
        val foundSender = addressService.byHash(senderHash)
        if (foundSender == null) {
            log.warn { "Transaction.verify() ... Unknown address: ${senderHash.encodeBase64()}" }
            return false
        }
        if (!SignatureUtils.verify(text.toByteArray(), signature, foundSender.publicKey)) {
            log.warn { "Transaction.verify() ... Invalid signature." }
            return false
        }
        if (!Arrays.equals(hash, calculateTransactionHash())) {
            log.warn { "Transaction.verify() ... Invalid hash." }
            return false
        }
        return true
    }

    fun synchronize(node: Node) {
        log.debug { "synchronize(node=$node)" }
        transactionPool.addAll(http4k.get<List<Transaction>>(node.address.toString() + "/transaction"))
    }
}


@RestController
@RequestMapping("transaction")
class TransactionController @Autowired constructor(
        private val transactionService: TransactionService,
        private val nodeService: NodeService
) {

    private val log = LOG {}

    @RequestMapping
    fun getTransactions() = transactionService.all()

    @RequestMapping(method = arrayOf(RequestMethod.PUT))
    internal fun addTransaction(
            @RequestBody transaction: Transaction,
            @RequestParam(required = false, defaultValue = "false") publish: Boolean,
            response: HttpServletResponse
    ) {
        log.debug {"addTransaction(transaction=$transaction, publish=$publish)"}
        val success = transactionService.add(transaction)

        if (success) {
            if (publish) {
                nodeService.broadcastPut("transaction", transaction)
            }
            response.status = HttpServletResponse.SC_ACCEPTED
        } else {
            response.status = HttpServletResponse.SC_NOT_ACCEPTABLE
        }
    }

}
