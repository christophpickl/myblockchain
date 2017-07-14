# About

Inspired by [jblockchain](https://github.com/neozo-software/jblockchain), just in order to learn rough concepts of blockchain.

## Commands


### List registered nodes

```
$ curl http://localhost:8080/address
```

### Register new node

```
$ curl -H 'Content-Type: application/json' -X PUT -d '{}' http://localhost:8080/address
```

Or add `?publish=true` to broadcast all.
