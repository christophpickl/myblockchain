package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.myblockchain.common.SignatureUtils
import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.alert
import tornadofx.runLater
import java.nio.file.Files
import java.nio.file.Paths


class CryptService {

    private val log = LOG {}

    fun createKeys() {
        log.info("createKeys()")
        val keyPair = SignatureUtils.generateKeyPair()
        // TODO save dialog
        val keyPrivate = Paths.get("key.priv")
        val keyPublic = Paths.get("key.pub")
        Files.write(keyPrivate, keyPair.private.encoded)
        Files.write(keyPublic, keyPair.public.encoded)
        runLater {
            alert(
                    type = INFORMATION,
                    header = "Keys generated successfully",
                    content = "Saved keys to:" +
                            // parent == null :(
                            "\n${keyPrivate.toFile().absolutePath}" +
                            "\n${keyPublic.toFile().absolutePath}"
            )
        }
    }
}
