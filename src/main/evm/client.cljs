(ns co-who.evm.client
  (:require ["solid-js" :refer [useContext]]
            ["viem" :as v :refer [createWalletClient createPublicClient custom http]]
            ["viem/chains" :refer [mainnet]]
            ["@wagmi/core" :refer [getWalletClient getPublicClient watchClient watchPublicClient watchConnections]]
            ["../Context.cljs" :refer [AppContext]]
            ["../components/user.cljs" :as u]
            ["./walletconnect.cljs" :refer [config]]))

(defonce wallet-client (atom nil))
(defonce public-client (atom nil))
(defonce mainnet-client (atom (createPublicClient
                               {:chain mainnet
                                :transport (http)})))
(defonce unwatch-wallet (atom nil))
(defonce unwatch-public (atom nil))
(defonce unwatch-connections (atom nil))

(defn ^:async init-clients []
  (let [wc (js-await (getWalletClient config))
        pc (js-await (getPublicClient config))]
    (println "init c:" wc pc)
    (reset! wallet-client wc)
    (reset! public-client pc)
    (reset! unwatch-wallet (watchClient config  {:onChange (fn [client]
                                                             (println "change wallet: " client)
                                                             (reset! wallet-client client))}))
    (reset! unwatch-public (watchPublicClient config  {:onChange (fn [client]
                                                                   (println "change wallet: " client)
                                                                   (reset! public-client client))}))
    (reset! unwatch-connections (watchConnections config  {:onChange (fn [connections]
                                                                       (let [ctx (useContext AppContext)]
                                                                         (println "change connections: " ctx)
                                                                         (u/init-auth {:store (first ctx) :setStore (second ctx)})))}))
    wc))

(def default wallet-client)
