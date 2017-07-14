package com.github.christophpickl.myblockchain.server

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.myblockchain.common.DIFFICULTY
import com.github.christophpickl.myblockchain.common.MAX_TRANSACTIONS_PER_BLOCK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean


@Service
class MiningService @Autowired constructor(
        private val transactionService: TransactionService,
        private val nodeService: NodeService,
        private val blockService: BlockService
) : Runnable {

    private val log = LOG {}
    private val runMiner = AtomicBoolean(false)

    fun startMiner() {
        if (runMiner.compareAndSet(false, true)) {
            log.debug { "startMiner()" }
            Thread(this).start()
        } else {
            log.debug { "startMiner() ... already started" }
        }

    }

    fun stopMiner() {
        log.debug { "stopMiner()" }
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

    private fun mineBlock(): Block? {
        val previousBlockHash = blockService.lastBlock?.hash
        val transactions = transactionService.all().take(MAX_TRANSACTIONS_PER_BLOCK)
        if (transactions.isEmpty()) {
            log.debug { "mineBlock() ... sleeping as of empty transactions" }
            Thread.sleep(10_000)
            return null
        }

        var tries = 0L
        while (runMiner.get()) {
            val block = Block(previousBlockHash, transactions, tries)
            if (block.getLeadingZerosCount() >= DIFFICULTY) {
                log.debug { "mineBlock() ... found block with proper difficulty" }
                return block
            }
            tries++
        }
        log.debug { "mineBlock() ... interrupted" }
        return null
    }

}
