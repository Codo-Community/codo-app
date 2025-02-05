(ns co-who.evm.client
  (:require ["viem" :as v :refer [createWalletClient createPublicClient custom http]]
            ["viem/chains" :refer [mainnet]]
            ["@wagmi/core" :refer [getPublicClient getWalletClient watchClient watchPublicClient watchConnections getConnectorClient getConnections getAccount]]
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
  (let [wc  (js-await (getWalletClient config)) #_(js-await (getConnectorClient config))]
    (let [;wc (js-await (getWalletClient config))
          pc (js-await (getPublicClient config))]
      (println "init c:" wc pc)
      (reset! wallet-client wc)
      (reset! public-client pc)
      wc)))

(defn get-account []
  (getAccount config))

(def default wallet-client)

;; Function to watch for wallet connection changes
#_(defn watch-wallet-connection []
  (let [unwatch (watchConnections
                 (fn [connections]
                   (println "Wallet connections updated:" connections)
                   (if (seq connections)
                     (do
                       (js-await (init-clients))
                       (println "Updated wallet-client:" @wallet-client))
                     (do
                       (reset! wallet-client nil)
                       (reset! public-client nil)
                       (println "Disconnected wallet")))))]
    (reset! unwatch-connections unwatch)))

;; Function to watch public client updates
#_(defn watch-public-client []
  (let [unwatch (watchPublicClient
                  (fn [new-client]
                    (println "Public client updated:" new-client)
                    (reset! public-client new-client)))]
    (reset! unwatch-public unwatch)))

;; Function to watch wallet client updates
#_(defn watch-wallet-client []
  (let [unwatch (watchClient
                  (fn [new-client]
                    (println "Wallet client updated:" new-client)
                    (reset! wallet-client new-client)))]
    (reset! unwatch-wallet unwatch)))
