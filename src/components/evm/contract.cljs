(ns components.evm.contract
  (:require ["solid-js" :refer [createMemo useContext]]
            ["../blueprint/button.jsx" :as b]
            ["../blueprint/dropdown.jsx" :as d]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../Context.mjs" :refer [AppContext]]))

#_(defn select-on-change [{:keys [store setStore] :as ctx} id]
  (setStore :counter/id
              (fn [counters]
                (update-in counters [id :counter/value] inc))))

(defn Contract [ident]
  (let [ctx (useContext AppContext)
        {:keys [store setStore]} ctx
        data (createMemo (fn []
                           (n/pull [:contract/id :contract/address :contract/chain :contract/name {:contract/abi [:name :type :stateMutability :inputs :outputs]}]
                                   (get-in store ident.children))))
        d (println "d " (data))]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 w-full gap-3"}
          [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
          [:span {:class "flex w-full gap-3"}
           #_[in/address-input {:name "Address"
                                :value address}]]
          (d/dropdown-select "Function" (mapv (fn [a]
                                                {:ident "none"
                                                 :value (:name a)})
                                              (filterv #(= (:type %) :function) (:contract/abi (data)))) (fn [e]) #_select-on-change (fn [e]) #_selected-function "")
          [:div {:class "flex items-end"}
           (b/button {:title "Add"
                      :on-click (fn [e]) #_on-click})]]))
