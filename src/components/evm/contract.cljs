(ns components.evm.contract
  (:require ["solid-js" :refer [createMemo useContext]]
            ["../blueprint/button.jsx" :as b]
            ["../blueprint/dropdown.jsx" :as d]
            ["./inputs.jsx" :as in]
            ["./transaction.jsx" :as tr]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../Context.mjs" :refer [AppContext]]))

(defn select-on-change [{:keys [store setStore] :as ctx} ident]
  (fn [e]
    (setStore (first ident)
              (fn [x]
                (assoc-in x [(second ident) :local/selected-function] e.target.value)))))

(defn add-transaction [{:keys [store setStore] :as ctx} contract]
  (fn [e]
    (let [selected (:local/selected-function contract)
          function-data (n/pull store (get-in store [:function/id selected]) [:name :inputs :outputs :stateMutability :type])
          transaction-data {:transaction/id (js/crypto.randomUUID)
                            :transaction/function (assoc function-data :function/id (js/crypto.randomUUID))}]
      (add ctx transaction-data)
      (setStore :transaction-builder
                (fn [x]
                  (update-in x [:transactions] conj [:transaction/id (:transaction/id transaction-data)]))))))

(defn Contract [ident]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(n/pull store (get-in store ident.children)
                                  [:contract/id :contract/address :contract/chain :contract/name
                                   :local/selected-function
                                   {:contract/abi [:name :type :stateMutability :inputs :outputs]}]))
        on-change (select-on-change ctx ident.children)]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 w-full gap-2"}
          [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
          [:span {:class "flex w-full gap-2"}
           (in/address-input {:name "Address"
                              :value (:contract/address (data))})]
          (d/dropdown-select "Function" (mapv (fn [a] {:id (:name a)
                                                       :value (:name a)})
                                              (filterv #(= (:type %) :function) (:contract/abi (data))))
                             on-change (:local/selected-function (data)))
          [:div {:class "flex items-end"}
           (b/button "Add" (add-transaction ctx (data)))]]))
