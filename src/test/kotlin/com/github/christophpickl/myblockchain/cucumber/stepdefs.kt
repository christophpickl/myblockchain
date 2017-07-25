package com.github.christophpickl.myblockchain.cucumber

import com.github.christophpickl.kpotpourri.http4k.Http4k
import com.github.christophpickl.kpotpourri.http4k.Response4k
import com.github.christophpickl.kpotpourri.http4k.buildHttp4k
import com.github.christophpickl.kpotpourri.http4k.get
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import cucumber.api.java.en.Then
import cucumber.api.java.en.When

// https://github.com/deadmoto/kotlin-cucumber-example/blob/master/src/test/kotlin/CucumberTest.kt

class NodeStepdefs {

    private val http4k: Http4k by lazy {
        buildHttp4k {
            baseUrlBy("http://localhost:8080")
        }
    }

    private lateinit var recentResponse: Response4k
    @When("^execute (.*) (.*)$")
    fun `When execute some HTTP request`(method: String, url: String) {
        // TODO support http4k method as param
        recentResponse = http4k.get(url)
    }

    @Then("^the response body is equal to '(.*)'$")
    fun `Then the response body is equal to _`(expectedBody: String) {
        assertThat(recentResponse.bodyAsString, equalTo(expectedBody))
    }

}
