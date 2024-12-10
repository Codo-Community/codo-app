(ns co-who.evm.client
  (:require ["viem" :as v :refer [createWalletClient createPublicClient custom http]]
            ["viem/chains" :refer [mainnet]]
            ["@wagmi/core" :refer [getWalletClient getPublicClient watchClient watchPublicClient watchConnections]]
            ["./walletconnect.cljs" :refer [config]]))

(defonce wallet-client (atom nil))
(defonce public-client (atom nil))
(defonce mainnet-client (atom (createPublicClient
                               {:chain mainnet
                                :transport (http (:href (js/URL. (str "/v2/" js/import.meta.env.VITE_ALCHEMY_API_KEY) js/import.meta.env.VITE_ALCHEMY_MAINNET)))})))
(defonce unwatch-wallet (atom nil))
(defonce unwatch-public (atom nil))
(defonce unwatch-connections (atom nil))

(defn ^:async init-clients []
  (let [wc (js-await (getWalletClient config))
        pc (js-await (getPublicClient config))]
    (println "init c:" wc pc)
    (reset! wallet-client wc)
    (reset! public-client pc)
    wc))

(def default wallet-client)
