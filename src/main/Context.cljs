(ns Context
  (:require ["solid-js" :refer [createContext]]
            ["solid-js/store" :refer [createStore]]
            ["./normad.cljs" :as norm]))

(def AppContext (createContext))

(defn init-context []
  (let [[store setStore] (createStore {:component/id {:header {:component/id :header
                                                               :user {:user/id 0
                                                                      :user/ethereum-address "0x0"}}
                                                      :project-wizard {:project []}
                                                      :project-list {:projects []}}
                                       :pages/id {:profile {:user {:user/id 0
                                                                   :user/ethereum-address "0x0"}}
                                                  :transaction-builder {:contracts []
                                                                        :contract nil
                                                                        :transactions []}}})
        {:keys [store setStore] :as ctx} (norm/add {:store store :setStore setStore})]
    (set! (.-store js/window) store)
    ctx))

(def default AppContext)
