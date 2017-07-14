package com.github.christophpickl.myblockchain

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Inject
import javafx.application.Application
import tornadofx.App
import tornadofx.Controller
import tornadofx.DIContainer
import tornadofx.EventBus.RunOn.BackgroundThread
import tornadofx.FX
import tornadofx.FXEvent
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.addClass
import tornadofx.button
import tornadofx.cssclass
import tornadofx.label
import tornadofx.px
import tornadofx.vbox
import kotlin.reflect.KClass

private val log = LOG {}

fun main(args: Array<String>) {
    log.info("Starting up client.")
    Application.launch(ClientTornadoApp::class.java, *args)
}

class ClientModule : AbstractModule() {
    override fun configure() {
        bind(CryptService::class.java).asEagerSingleton()
        bind(MainController::class.java).asEagerSingleton()
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

class MainView : View() {

    override val root = vbox {
        label("Hello MyBlockchain")
        button("Generate key pair") {
            setOnAction {
                fire(GenerateKeyPairRequest)
            }
        }
    }

    init {
        root.addClass(Styles.mainViewCssClass)
    }
}

class CryptService {
    private val log = LOG {}
    fun createKeys() {
        log.info("createKeys()")
    }
}

class MainController @Inject constructor(
        private val cryptService: CryptService
) : Controller() {
    private val logg = LOG {}
    private val mainView: MainView by inject()
    // private val cryptService: CryptService by di()

    init {
        subscribe<GenerateKeyPairRequest> {
            logg.debug("subscribed event dispatched: GenerateKeyPairRequest")
            cryptService.createKeys()
        }
    }

}
object GenerateKeyPairRequest : FXEvent(BackgroundThread)

class Styles : Stylesheet() {
    companion object {
        val mainViewCssClass by cssclass()
    }
    init {
        select(mainViewCssClass) {
            padding = tornadofx.box(15.px)
            vgap = 7.px
            hgap = 10.px
        }
    }
}
