(ns start
  (:require ["./evm/client.mjs" :as c]
            ["./composedb/auth.mjs" :as a]
            ["flowbite" :as fb]))

(defn start []

  (fb/initFlowbite)

  (.then (a/init-auth)
         #(a/authenticate-user)))

(def default start)
