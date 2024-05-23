(ns components.evm.contract
  (:require ["solid-js" :refer [createMemo useContext createSignal]]
            ["../blueprint/button.jsx" :as b]
            ["../blueprint/dropdown.jsx" :as d]
            ["./inputs.jsx" :as in]
            ["./transaction.jsx" :as tr]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../Context.mjs" :refer [AppContext]]
            ["../../comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

#_(defn select-on-change [{:keys [store setStore] :as ctx} ident]
  (fn [e]
    (setStore (first ident)
              (fn [x]
                (assoc-in x [(second ident) :local/selected-function] e.target.value)))))

(defn add-transaction [{:keys [store setStore] :as ctx} local]
  (fn [e]
    (let [selected (:selected-function (local))
          function-data (n/pull store (get-in store [:function/id selected]) [:name :inputs :outputs :stateMutability :type])
          transaction-data {:transaction/id (js/crypto.randomUUID)
                            :transaction/function (assoc function-data :function/id (js/crypto.randomUUID))}]
      (add ctx transaction-data)
      (setStore :pages/id
                (fn [x]
                  (update-in x [:transaction-builder :transactions] conj [:transaction/id (:transaction/id transaction-data)]))))))

(defc Contract [this {:contract/keys [id address chain name
                                      {abi [:name :type :stateMutability :inputs :outputs]}]}]
  (let [[local setLocal] (createSignal {:selected-function ""})]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 w-full gap-2"}
          [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
          [:span {:class "flex w-full gap-2"}
           #jsx [in/address-input (fn [] {:name "Address"
                                          :readonly true
                                          :value (address)})]]
          [d/dropdown-select {:title "Function"
                              :items #(mapv (fn [a] {:id (:name a)
                                                     :value (:name a)})
                                            (filterv (fn [x] (= (:type x) :function)) (abi)))
                              :on-change (fn [e] (setLocal {:selected-function e.target.value}))
                              :selected #(:selected-function (local))}]
          [:div {:class "flex items-end"}
           [b/button {:title "Add"
                      :on-click (add-transaction ctx local)}]]]))

(def ui-contract (comp/comp-factory Contract AppContext))
