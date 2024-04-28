(ns start
  (:require ["./evm/client.mjs" :as c]
            ["./evm/util.mjs" :as eu]
            ["flowbite" :as fb]))

(defn start []
  (println "wallet: " c/wallet-client)
  (fb/initFlowbite)
  )

(def default start)
