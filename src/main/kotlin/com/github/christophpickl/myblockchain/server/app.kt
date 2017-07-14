package com.github.christophpickl.myblockchain.server

import com.github.christophpickl.kpotpourri.common.logging.LOG
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

private val log = LOG {}

@SpringBootApplication
class MyBlockchainSpringBootApplication

fun main(args: Array<String>) {
    val port = if (args.size >= 1) args[0].toInt() else 8080
    log.info { "Starting up spring boot application on port $port..." }
    SpringApplicationBuilder()
            .sources(MyBlockchainSpringBootApplication::class.java)
            .bannerMode(Banner.Mode.OFF)
            .properties(hashMapOf<String, Any>("server.port" to port))
            .run(*args)
}
