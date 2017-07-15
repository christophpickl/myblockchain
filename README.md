# About

This playground is based on another GitHub project called [jblockchain](https://github.com/neozo-software/jblockchain).
It's sole purpose is to learn the rough concepts of blockchain.

## Endpoints

| Endpoint                 | Description                     |
| ------------------------ | ------------------------------- |
| `GET /address`           | List all registered addresses.  |
| `PUT /address`           | Register new address.  |
| `GET /block`             | List all mined blocks.  |
| `PUT /block`*             | Register mined block.  |
| `GET /block/start-miner` | Start background mining thread on this node.  |
| `GET /block/stop-miner` | Stop thread on next cycle.  |
| `GET /node`             | List all known other nodes.  |
| `PUT /node`*             | Register new node.  |
| `POST /node/remove`*             | Unregister node.  |
| `GET /node/ip`*             | Get the IP address of the current node. |
| `GET /transaction`           | List all transactions in the current pool.  |
| `PUT /transaction`           | Add new transaction.  |

`*` ... Intended to be invoked only internally by the blockchain's nodes.

## Data model

### Address
### Node
### Block
### Transaction

# Build and run

```bash
$ ./gradlew build
$ java -jar build/libs/myblockchain.jar [8080]
```

You need to run at least one node on the default port 8080 as it represents the master node, which
is required for running any further nodes as they will try to connect that one.

# Further reading

* Interesting [article on heise.de](https://www.heise.de/solutions/ibm-blockchain/wie-blockchains-fuer-mehr-vertrauen-in-der-lieferkette-sorgen-koennen/) about usecases of this technology (German) 
