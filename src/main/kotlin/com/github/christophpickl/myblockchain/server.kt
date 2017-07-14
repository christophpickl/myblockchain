package com.github.christophpickl.myblockchain

import com.github.christophpickl.kpotpourri.common.logging.LOG
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val log = LOG {}

@SpringBootApplication
class MyBlockchainSpringBootApplication

fun main(args: Array<String>) {
    log.info { "Starting up spring boot application ..." }
    SpringApplication.run(MyBlockchainSpringBootApplication::class.java, *args)
}

@RestController
class MyController {
    @RequestMapping("/")
    fun index(): String {
        return "hallo"
    }

    @RequestMapping("/transaction")
    fun transaction() = listOf(
            Transaction("id1"),
            Transaction("id2")
    )
}

data class Transaction(
        val id: String
)
