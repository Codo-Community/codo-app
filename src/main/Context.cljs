(ns Context
  (:require ["solid-js" :refer [createContext]]
            ["solid-js/store" :refer [createStore]]
            ["./normad.cljs" :as norm]))

(def AppContext (createContext))

(defn init-context []
  (let [[store setStore] (createStore {:component/id {:header {:component/id :header
                                                               :chain {:chain/id 31337
                                                                       :chain/name "Hardhat"}
                                                               :user {:user/id 0
                                                                      :user/ethereum-address "0x0"}}
                                                      :project-wizard {:project []}
                                                      :project-list {:projects []}}
                                       :pages/id {:profile {:user {:user/id 0
                                                                   :user/ethereum-address "0x0"}}
                                                  :transaction-builder {:contracts []
                                                                        :contract nil
                                                                        :transactions []}}})
        ctx {:store store :setStore setStore}]
    (norm/add ctx)
    (set! (.-store js/window) store)
    ctx))

(def default AppContext)
