(ns co-who.evm.lib
  (:require ["viem" :as viem :refer [getContract getAbiItem]]
            ["./client.mjs" :refer [wallet-client public-client]]
            ["./abi.mjs" :refer [token-abi]]))

(defn get-contract [data]
  (getContract data))

(defn simulate-contract [public-client account address abi function-name & args]
  (public-client.simulateContract {:address address :abi abi :account account :functionName function-name
                                   :args args}))

(defn ^:async deploy-contract [wallet-client abi account bytecode]
  (.deployContract wallet-client {:abi abi :account account :bytecode bytecode}))
