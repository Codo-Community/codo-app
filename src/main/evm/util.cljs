(ns co-who.evm.util
  (:require ["viem" :refer [parseEther]]
            ["viem/accounts" :as ac :refer [generatePrivateKey privateKeyToAccount]]))

#_(set! *warn-on-infer* false)

(defn generate-private-key []
  (generatePrivateKey))

(defn account-from-private-key [key]
  (privateKeyToAccount key))

(defn ^:async request-addresses  [client f]
  (.then (.requestAddresses client) f))

(defn ^:async get-address [client f]
  (.then (.getAddresses client)
         f))

#_(defn ^:async get-chain [client]
  (.then (.get client)
         #(first %)))

(defn add-listener [ethereum event handler]
  (.. js/window.ethereum (on event handler)))

(defn add-accounts-changed [f]
  (add-listener js/window.ethereum "accountsChanged" f))

(defn add-chain-changed [f]
  (add-listener js/window.ethereum "chainChanged" f))

(defn parse-ether [value]
  (parseEther value))

(defn ^:async get-chain [& f]
  (.then (.request js/window.ethereum {:method "eth_chainId"})
         (or f (fn [r] (js/parseInt r 16)))))
