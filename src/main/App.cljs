(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["./router.cljs" :as r]
            ["./composedb/client.cljs" :as cdb]
            ["./composedb/auth.cljs" :as cda]
            ["./composedb/data_feed.cljs" :as cdf]
            ["./composedb/events.cljs" :as events]
            ["./comp.cljs" :as comp :refer [Comp]]
            ["./Context.cljs" :as context :refer [AppContext]]
            ["flowbite" :as fb])
  (:require-macros [comp :refer [defc]]))

(defc Root [this {:keys []}]
  (let [ctx  (context/init-context)]
    (onMount #(do
                ;; TODO: fetch abi here
                (println "init")
                (cdf/init-listeners ctx events/handle)
                (.then (cda/init-auth)
                       (fn [r] (.then (cdb/init-clients)
                                      (fn [r]))))
                (fb/initFlowbite)))
      #jsx [AppContext.Provider {:value ctx}
            #jsx [r/ui-router]]))

(def ui-root (comp/comp-factory Root AppContext))
