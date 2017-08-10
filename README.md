# About

This playground is based on another GitHub project called [jblockchain](https://github.com/neozo-software/jblockchain).
It's sole purpose is to learn the rough concepts of blockchain by implementing some kind of public chat.

## Endpoints

| Endpoint                 | Description                                  | Code     |
| ------------------------ | -------------------------------------------- | -------- |
| `GET /address`           | List all registered addresses.               |  200     |
| `PUT /address`           | Register new address.                        |  202/406 |
| `GET /block`             | List all mined blocks.                       |  200     |
| `PUT /block`*            | Register mined block.                        |  202/406 |
| `GET /block/start-miner` | Start background mining thread on this node. |  200     |
| `GET /block/stop-miner`  | Stop thread on next cycle.                   |  200     |
| `GET /node`              | List all known other nodes.                  |  200     |
| `PUT /node`*             | Register new node.                           |  200     |
| `POST /node/remove`*     | Unregister node.                             |  200     |
| `GET /node/ip`*          | Get the IP address of the current node.      |  200     |
| `GET /transaction`       | List all transactions in the current pool.   |  200     |
| `PUT /transaction`       | Add new transaction.                         |  202/406 |

`*` ... Intended to be invoked only internally by the blockchain's nodes.

## Data model

| Data Object | Property     | Type          | Description                                    |
| ----------- | ------------ | ------------- | ---------------------------------------------- |
| Address     |              |               | Represents a user.                             |
|             | name         | String        | The human readable name.                       |
|             | public key   | Byte[]        | Public part of the generated key pair.         |
|             | hash         | Byte[]        | Hash of name and public key.                   |
| Node        |              |               |                                                |
|             | address      | URL           | The IP and port of the node.                   |
| Block       |              |               |                                                |
|             | previous     | Byte[]?       | Hash of the previous block.                    |
|             | transactions | Transaction[] | List of transactions in this block.            |
|             | tries        | Number        | How many cycles did the mining take.           |
|             | timestamp    | DateTime      | Date of creation.                              |
|             | merkle root  | Byte[]        | Hash over all transaction hashes.              |
|             | hash         | Byte[]        | Hash of previous, merkle, tries and timestamp. |
| Transaction |              |               |                                                |
|             | text         | String        | The actual payload, chat message in this case. |
|             | sender       | Byte[]        | The `Address.hash` which created this tx.      |
|             | signature    | Byte[]        | The signed `text` hashed with the private key. |
|             | timestamp    | DateTime      | Date of creation.                              |
|             | hash         | Byte[]        | Hash of all attributes.                        |

# Build and run

```bash
$ ./gradlew build
$ java -jar build/libs/myblockchain.jar [8080]
```

You need to run at least one node on the default port 8080 as it represents the master node, which
is required for running any further nodes as they will try to connect that one.

# Further reading

* Interesting [article on heise.de](https://www.heise.de/solutions/ibm-blockchain/wie-blockchains-fuer-mehr-vertrauen-in-der-lieferkette-sorgen-koennen/) about usecases of this technology (German) 
