(ns components.evm.contract
  (:require ["solid-js" :refer [createMemo useContext]]
            ["../blueprint/button.jsx" :as b]
            ["../../Context.mjs" :refer [AppContext]]))

#_(defn select-on-change [{:keys [store setStore] :as ctx} id]
  (setStore :counter/id
              (fn [counters]
                (update-in counters [id :counter/value] inc))))

(defn Contract [ident]
  (let [ctx (useContext AppContext)
        {:keys [store setStore]} ctx
        data (createMemo (fn []
                           (println "run memo user id " ident.children)
                           (get-in store ident.children)))]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 w-full gap-3"}
          [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
          [:span {:class "flex w-full gap-3"}
           #_[in/address-input {:name "Address"
                                :value address}]]
          [:div {:class "flex items-end"}
           (b/button {:title "Add"
                      :on-click (fn [e]) #_on-click})]]))
