## Rest 接口
注册
```
curl -XPOST  localhost:7050/registrar -d '{
  "enrollId": "jim",
  "enrollSecret": "6avZQLwcUe9b"
}'
```
部署chaincode
```
curl -XPOST localhost:7050/chaincode -d '{
 "jsonrpc": "2.0",
   "method": "deploy",
   "params": {
     "type": 1,
     "chaincodeID":{
         "name": "mycc"
     },
     "ctorMsg": {
         "args":["init", "a", "100", "b", "200"]
     }
   },
   "id": 1
 }'
```

或者:
```
curl -XPOST localhost:7050/chaincode -d '{
  "jsonrpc": "2.0",
  "method": "deploy",
  "params": {
    "type": 1,
    "chaincodeID":{
        "name": "mycc"
    },
    "ctorMsg": {
        "args":["init", "a", "100", "b", "200"]
    }，
    "secureContext": "jim"
  },
  "id": 1
}'
```
调用chaincode
```
curl -XPOST localhost:7050/chaincode -d '{
  "jsonrpc": "2.0",
  "method": "invoke",
  "params": {
      "type": 1,
      "chaincodeID":{
          "name":"mycc"
      },
      "ctorMsg": {
         "args":["invoke", "a", "b", "10"]
      }
  },
  "id": 3
  }'
  ```
## Refenrence

[Chaincode-setup](https://hyperledger-fabric.readthedocs.io/en/latest/Setup/Chaincode-setup/)
