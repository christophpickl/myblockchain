package com.github.christophpickl.myblockchain.cucumber

import cucumber.api.CucumberOptions
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

// https://github.com/deadmoto/kotlin-cucumber-example/blob/master/src/test/kotlin/CucumberTest.kt

class MyStepdefs {
    @When("^the step is implemented$")
    fun `When the step is implemented`() {
        println("when the")
    }
    @Then("^the next step is executed$")
    fun `Then the next step is executed`() {
        println("then next")
    }
}

@RunWith(Cucumber::class)
@CucumberOptions(
        format = arrayOf("pretty"),
//        glue = arrayOf("com.lunivore.montecarluni.glue"),
        features = arrayOf("."))
class Runner
