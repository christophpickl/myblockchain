package com.github.christophpickl.myblockchain.server

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
