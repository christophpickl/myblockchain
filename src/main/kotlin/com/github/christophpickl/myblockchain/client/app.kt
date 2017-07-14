package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javafx.application.Application
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import kotlin.reflect.KClass

private val log = LOG {}

fun main(args: Array<String>) {
    log.info("Starting up client.")
    Application.launch(ClientTornadoApp::class.java, *args)
}

class ClientModule : AbstractModule() {
    override fun configure() {
        bind(CryptService::class.java).asEagerSingleton()
        bind(KeysController::class.java).asEagerSingleton()
    }
}

class ClientTornadoApp : App(MainView::class, Styles::class) {
    init {
        val guice = Guice.createInjector(ClientModule())
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) =
                    guice.getInstance(type.java)
        }
    }
}

