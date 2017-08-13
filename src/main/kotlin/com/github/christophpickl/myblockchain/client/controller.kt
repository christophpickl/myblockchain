package com.github.christophpickl.myblockchain.client

import com.github.christophpickl.myblockchain.common.SignatureUtils
import com.github.christophpickl.myblockchain.server.Address
import com.github.christophpickl.myblockchain.server.Transaction
import com.google.inject.Inject
import tornadofx.*
import tornadofx.EventBus.RunOn.BackgroundThread
import java.io.File

object ListNodesRequest : FXEvent(BackgroundThread)
object ListAddressesRequest : FXEvent(BackgroundThread)
object ListTransactionsRequest : FXEvent(BackgroundThread)
object ListBlocksRequest : FXEvent(BackgroundThread)

object StartMinerRequest : FXEvent(BackgroundThread)
object StopMinerRequest : FXEvent(BackgroundThread)

object CreateAddressRequest : FXEvent(BackgroundThread)
object CreateTransactionRequest : FXEvent(BackgroundThread)


class GeneralController @Inject constructor(
        private val client: BlockchainClient
) : Controller() {

    private val mainView: MainView by inject()
    private var recentAddress: Address? = null

    init {
        subscribe<ListNodesRequest> {
            mainView.writeOutput(client.executeGet("/node"))
        }
        subscribe<ListAddressesRequest> {
            mainView.writeOutput(client.executeGet("/address"))
        }
        subscribe<ListTransactionsRequest> {
            mainView.writeOutput(client.executeGet("/transaction"))
        }
        subscribe<ListBlocksRequest> {
            mainView.writeOutput(client.executeGet("/block"))
        }
        subscribe<StartMinerRequest> {
            client.executeGet("/block/start-miner")
            mainView.output.text = "Miner started"
        }
        subscribe<StopMinerRequest> {
            client.executeGet("/block/stop-miner")
            mainView.output.text = "Miner stopped"
        }
        subscribe<CreateAddressRequest> {
            recentAddress = Address(
                    name = mainView.addressName.text,
                    publicKey = File(mainView.pathKeyPublic.text).readBytes()
            )
            client.addAddress(recentAddress!!)
            mainView.output.text = "Successfully added: $recentAddress"
        }
        subscribe<CreateTransactionRequest> {
            if (recentAddress == null) {
                mainView.output.text = "No address was yet stored!"
            } else {
                val txText = mainView.transactionText.text
                val transaction = Transaction(
                        text = txText,
                        senderHash = recentAddress!!.hash,
                        signature = SignatureUtils.sign(txText.toByteArray(), File(mainView.pathKeyPrivate.text).readBytes())
                )
                client.addTransaction(transaction)
                mainView.output.text = "Successfully added: $transaction"
            }
        }
    }

}
