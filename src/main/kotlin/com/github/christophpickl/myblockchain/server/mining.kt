package com.github.christophpickl.myblockchain.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

val MAX_TRANSACTIONS_PER_BLOCK = 5
val DIFFICULTY = 3


@Service
class MiningService @Autowired constructor(
        private val transactionService: TransactionService,
        private val nodeService: NodeService,
        private val blockService: BlockService
) : Runnable {

    private val runMiner = AtomicBoolean(false)

    fun startMiner() {
        if (runMiner.compareAndSet(false, true)) {
            Thread(this).start()
        }

    }

    fun stopMiner() {
        runMiner.set(false)
    }

    override fun run() {
        while (runMiner.get()) {
            val block = mineBlock()
            if (block != null) {
                blockService.append(block)
                nodeService.broadcastPut("block", block)
            }
        }
    }

    // TODO add logging via AOP
    private fun mineBlock(): Block? {
        val previousBlockHash = blockService.lastBlock?.hash
        val transactions = transactionService.all().take(MAX_TRANSACTIONS_PER_BLOCK)
        if (transactions.isEmpty()) {
            Thread.sleep(10_000)
            return null
        }

        var tries = 0L
        while (runMiner.get()) {
            val block = Block(previousBlockHash, transactions, tries)
            if (block.getLeadingZerosCount() >= DIFFICULTY) {
                return block
            }
            tries++
        }
        return null
    }

}
