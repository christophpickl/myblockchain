package com.github.christophpickl.myblockchain

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javafx.application.Application
import javafx.scene.text.FontWeight
import tornadofx.App
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.c
import tornadofx.hbox
import tornadofx.label
import tornadofx.px

private val log = LOG {}

fun main(args: Array<String>) {
    log.info("Starting up guice.")
    val guice = Guice.createInjector(ClientModule())
    val client = guice.getInstance(ClientApp::class.java)
    client.start(args)
}

class ClientModule : AbstractModule() {
    override fun configure() {
        bind(ClientApp::class.java)
    }
}

class ClientApp {

    private val log = LOG {}

    fun start(args: Array<String>) {
        log.info("Starting up UI.")
        Application.launch(ClientViewStarter::class.java, *args)
    }

}

class ClientViewStarter : App(MainView::class)

class MainView : View() {
    override val root = hbox {
        label("Hello MyBlockchain")
    }

}
