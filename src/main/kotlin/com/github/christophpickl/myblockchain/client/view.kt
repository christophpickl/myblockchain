package com.github.christophpickl.myblockchain.client

import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.addClass
import tornadofx.button
import tornadofx.cssclass
import tornadofx.label
import tornadofx.px
import tornadofx.vbox


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
