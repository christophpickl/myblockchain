package com.github.christophpickl.myblockchain.client

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.addClass
import tornadofx.button
import tornadofx.cssclass
import tornadofx.label
import tornadofx.px
import tornadofx.singleAssign
import tornadofx.textarea
import tornadofx.textfield
import tornadofx.vbox
import java.nio.file.Path


class MainView : View() {

    var pathKeyPrivate: TextField by singleAssign()
    var pathKeyPublic: TextField by singleAssign()
    var output: TextArea by singleAssign()

    override val root = vbox {
        label("Hello MyBlockchain")
        button("Generate key pair") {
            setOnAction {
                fire(GenerateKeyPairRequest)
            }
        }
        pathKeyPrivate = textfield { isEditable = false }
        pathKeyPublic = textfield { isEditable = false }
        output = textarea {
            prefRowCount = 8
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

fun TextField.textBy(path: Path) {
    text = path.toFile().absolutePath
}
