(ns start
  (:require ["flowbite" :as fb]))

(defn start []

  (js/console.log "start")

  #_(d/authenticate)

  (fb/initFlowbite)

  #_(.then (a/init-auth)
         #(a/authenticate-user)))

(def default start)
