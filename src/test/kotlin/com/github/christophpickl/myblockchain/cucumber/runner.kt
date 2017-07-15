package com.github.christophpickl.myblockchain.cucumber

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        format = arrayOf("pretty"),
        features = arrayOf("."))
class Runner
