package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.inject.Inject
import tornadofx.Controller


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
