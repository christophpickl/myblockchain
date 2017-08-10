package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.http4k.Response4k
import com.github.christophpickl.myblockchain.common.objectMapper
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import tornadofx.Stylesheet
import tornadofx.View
import tornadofx.addClass
import tornadofx.button
import tornadofx.cssclass
import tornadofx.hbox
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
    var addressName: TextField by singleAssign()
    var transactionText: TextField by singleAssign()

    var output: TextArea by singleAssign()

    override val root = vbox {
        label("Hello MyBlockchain")
        button("Generate key pair") { setOnAction { fire(GenerateKeyPairRequest) } }
        hbox {
            label("Path private key:")
            pathKeyPrivate = textfield { isEditable = false }
        }
        hbox {
            label("Path public key:")
            pathKeyPublic = textfield { isEditable = false }
        }

        hbox {
            label("List =>")
            button("Nodes") { setOnAction { fire(ListNodesRequest) } }
            button("Addresses") { setOnAction { fire(ListAddressesRequest) } }
            button("Transactions") { setOnAction { fire(ListTransactionsRequest) } }
            button("Blocks") { setOnAction { fire(ListBlocksRequest) } }
        }
        hbox {
            label("Miner =>")
            button("Start") { setOnAction { fire(StartMinerRequest) } }
            button("Stop") { setOnAction { fire(StopMinerRequest) } }
        }
        hbox {
            label("Address =>")
            label("Name: ")
            addressName = textfield()
            button("Create") { setOnAction { fire(CreateAddressRequest) } }
        }
        hbox {
            label("Transaction =>")
            transactionText = textfield()
            button("Create") { setOnAction { fire(CreateTransactionRequest) } }
        }

        label("Output:")
        output = textarea {
            prefRowCount = 20
        }
    }

    init {
        root.addClass(Styles.mainViewCssClass)
    }

    fun writeOutput(response: Response4k) {
        val json = objectMapper.readValue(response.bodyAsString, Any::class.java)
        output.text = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)
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
