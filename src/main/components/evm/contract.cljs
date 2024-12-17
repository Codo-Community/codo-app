(ns components.evm.contract
  (:require ["solid-js" :refer [createMemo useContext createSignal]]
            ["../blueprint/button.cljs" :as b]
            ["../blueprint/dropdown.cljs" :as d]
            ["./inputs.cljs" :as in]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

#_(defn select-on-change [{:keys [store setStore] :as ctx} ident]
  (fn [e]
    (setStore (first ident)
              (fn [x]
                (assoc-in x [(second ident) :local/selected-function] e.target.value)))))

(defn add-transaction [{:keys [store setStore] :as ctx} local]
  (fn [e]
    (let [selected (:selected-function (local))
          function-data (sqeave/pull store (get-in store [:function/id selected]) [:name :inputs :outputs :stateMutability :type])
          transaction-data {:transaction/id (js/crypto.randomUUID)
                            :transaction/function (assoc function-data :function/id (js/crypto.randomUUID))}]
      (sqeave/add! ctx transaction-data {:append [:component/id :transaction-builder :transactions]})
      #_(setStore :component/id
                (fn [x]
                  (update-in x [:transaction-builder :transactions] conj [:transaction/id (:transaction/id transaction-data)]))))))

(defc Contract [this {:contract/keys [id address chain name
                                      {abi [:name :type]}]}]
  (let [[local setLocal] (createSignal {:selected-function ""})]
    #jsx [:div {:class "flex flex-col w-full gap-3 mb-3"}
          [:h2 {:class "font-bold text-lg"} "Parameters"]
          [:span {:class "flex w-full gap-2"}
           [in/address-input {:name "Address"
                              :readonly true
                              :value address}]]
          [d/dropdown-select {:title "Function"
                              :items #(mapv (fn [a] {:id (:name a)
                                                     :value (:name a)})
                                            (filterv (fn [x] (= (:type x) :function)) (abi)))
                              :on-change (fn [e] (setLocal {:selected-function e.target.value}))
                              :selected #(:selected-function (local))}]
          [:div {:class "flex items-end"}
           [b/button {:title "Add"
                      :on-click (add-transaction ctx local)}]]]))
