package com.github.christophpickl.myblockchain

import com.github.christophpickl.kpotpourri.http4k.Http4k
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.github.christophpickl.myblockchain.server.MyBlockchainSpringBootApplication
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner


// https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = DEFINED_PORT,
        properties = arrayOf("server.port=8080", "spring.main.banner-mode=off"))
@ContextConfiguration(classes = arrayOf(MyBlockchainSpringBootApplication::class))
class FooIT {

    private val http4k: Http4k by lazy {
        buildHttp4k {
            baseUrlBy("http://localhost:8080")
        }
    }

    @Test
    @Ignore // would need to startup a running local server first
    fun `GET node ip - should return localhost's IP address`() {
       assertThat(http4k.get<String>("/node/ip"),
               equalTo("127.0.0.1"))
    }

}
