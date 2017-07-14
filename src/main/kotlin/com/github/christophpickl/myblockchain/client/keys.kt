package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.myblockchain.common.SignatureUtils
import com.google.inject.Inject
import javafx.scene.control.Alert.AlertType.INFORMATION
import tornadofx.Controller
import tornadofx.EventBus.RunOn.BackgroundThread
import tornadofx.FXEvent
import tornadofx.alert
import tornadofx.runLater
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


object GenerateKeyPairRequest : FXEvent(BackgroundThread)
class KeyPairGenerated() : FXEvent(BackgroundThread)

class KeysController @Inject constructor(
        private val cryptService: CryptService
) : Controller() {
    private val logg = LOG {}
    private val mainView: MainView by inject()
    // private val cryptService: CryptService by di()

    init {
        subscribe<GenerateKeyPairRequest> {
            logg.debug("subscribed event dispatched: GenerateKeyPairRequest")
            val (keyPrivate, keyPublic) = cryptService.createKeys()
            mainView.pathKeyPrivate.textBy(keyPrivate)
            mainView.pathKeyPublic.textBy(keyPublic)
        }
    }

}

class CryptService {

    private val log = LOG {}

    fun createKeys(): Pair<Path, Path> {
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
        return Pair(keyPrivate, keyPublic)
    }
}
