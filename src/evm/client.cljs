(ns co-who.evm.client
  (:require ["viem" :as v :refer [createWalletClient createPublicClient custom http]]
            ["viem/chains" :refer [sepolia hardhat]]))

(defonce chains [sepolia hardhat])

(defonce wallet-client (createWalletClient {:chain (first chains)
                                            :transport (custom js/window.ethereum)}))

(defonce public-client (createPublicClient {:chain (first chains)
                                            :transport (http)}))

(def default wallet-client)
