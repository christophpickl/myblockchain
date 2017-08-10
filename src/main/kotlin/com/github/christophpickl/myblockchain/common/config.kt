package com.github.christophpickl.myblockchain.common

const val MASTER_NODE_PORT = 8080
const val MASTER_NODE_ADDRESS = "http://localhost:$MASTER_NODE_PORT"
val MAX_TRANSACTIONS_PER_BLOCK = 5

// change difficulty in order to adjust time needed to mine a block
val DIFFICULTY = 3
