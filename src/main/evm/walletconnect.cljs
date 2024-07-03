#_(ns main.evm.walletconnect
  (:require ["@walletconnect/core" :refer [Core]]
            ["@walletconnect/web3wallet" :refer [Web3Wallet]]))

#_(defonce core (Core. {:projectId js/import.meta.env.WALLET_CONNECT_ID}))

#_(defonce wallet (js-await (.init Web3Wallet {:core core
                                             :metadata {:name "Codo"
                                                        :description "AppKit Ex"
                                                        :url "www.web3modal.com"
                                                        :icons []}})))
(ns main.evm.walletconnect
  (:require ["@web3modal/wagmi" :refer [defaultWagmiConfig]]
            ["viem/chains" :refer [sepolia hardhat mainnet polygon arbitrum]]
            ["@wagmi/core" :refer [reconnect]]
            ))

(def chains (if js/import.meta.env.PROD
              [mainnet polygon arbitrum]
              [sepolia hardhat]))

;; 1. Get a project ID at https://cloud.walletconnect.com
(def project-id js/import.meta.env.VITE_WALLET_CONNECT_PROJECT_ID)

;; 2. Create wagmiConfig
(def metadata {:name "Web3Modal"
               :description "Web3Modal Example"
               :url "https://localhost:3000" ;; origin must match your domain & subdomain.
               :icons ["https://avatars.githubusercontent.com/u/37784886"]})

(def config (defaultWagmiConfig {:chains chains
                                 :projectId project-id
                                 :metadata metadata
                                 :analytics false
                                 }))

#_(defonce unwatch (watchConnectors config  {:onChange (fn [connectors] (println "connectors: " connectors))}))

(reconnect config)

#_(.then (reconnect config)
       (fn [res] (get-session)))
