(ns start
  (:require ["./evm/client.mjs" :as c]
            ["../dev/dev.mjs" :as d]
            ["./composedb/auth.mjs" :as a]
            ["flowbite" :as fb]))

(defn start []

  (js/console.log "sada: a")

  #_(d/authenticate)

  (fb/initFlowbite)

  (.then (a/init-auth)
         #(a/authenticate-user)))

(def default start)
