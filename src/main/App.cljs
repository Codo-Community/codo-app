(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["./router.cljs" :as r]
            ["./composedb/client.cljs" :as cdb]
            ["./composedb/auth.cljs" :as cda]
            ["./comp.cljs" :as comp :refer [Comp]]
            ["./Context.cljs" :as context :refer [AppContext]]
            ["flowbite" :as fb])
  (:require-macros [comp :refer [defc]]))

(defc Root [this {:keys []}]
  (do (onMount #(do
                ;; TODO: fetch abi here
                  (.then (cda/init-auth)
                         (fn [r] (println r) (.then (cdb/init-clients)
                                                    (fn []))))
                  (fb/initFlowbite)))
      #jsx [AppContext.Provider {:value (context/init-context)}
            #jsx [r/ui-router]
            ]))

(def ui-root (comp/comp-factory Root AppContext))
