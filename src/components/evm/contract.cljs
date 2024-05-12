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
          a (println selected)
          function-data (n/pull store (get-in store [:function/id selected]))
          transaction-data {:transaction/id (js/crypto.randomUUID)
                            :transaction/function (assoc function-data :function/id (js/crypto.randomUUID))
                            }
          ]
      (add [store setStore] transaction-data)
      (setStore :transaction-builder
              (fn [x]
                (update-in x [:transactions] conj [:transaction/id (:transaction/id transaction-data)]))))))

(defn Contract [ident]
  (let [ctx (useContext AppContext)
        {:keys [store setStore]} ctx
        data (createMemo (fn []
                           (n/pull store (get-in store ident.children)
                                   [:contract/id :contract/address :contract/chain :contract/name
                                    {:contract/abi [:name :type :stateMutability :inputs :outputs]}])))
        on-change (select-on-change ctx ident.children)]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 w-full gap-2"}
          [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
          [:span {:class "flex w-full gap-2"}
           (in/address-input {:name "Address"
                              :value (:contract/address (data))})]
          (d/dropdown-select "Function" (mapv (fn [a] {:value (:name a)})
                                              (filterv #(= (:type %) :function) (:contract/abi (data))))
                             on-change "")
          [:div {:class "flex items-end"}
           (b/button "Add" (add-transaction ctx (data)))]]))
