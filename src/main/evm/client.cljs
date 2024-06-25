(ns co-who.evm.client
  (:require ["viem" :as v :refer [createWalletClient createPublicClient custom http]]
            ["./walletconnect.cljs" :refer [chains]]))

(defonce wallet-client (atom (createWalletClient {:chain (first chains)
                                                  :transport (custom js/window.ethereum)})))

(defonce public-client (atom (createPublicClient {:chain (first chains)
                                                  :transport (http)})))

(defn init-clients [wallet-client public-client chain]
  (reset! wallet-client (createWalletClient {:chain chain
                                             :transport (custom js/window.ethereum)}))
  (reset! public-client (createPublicClient {:chain chain
                                             :transport (http (get-in chain [:rpcUrls :default :http 0]))})))

(def default wallet-client)
