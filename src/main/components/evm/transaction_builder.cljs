(ns co-who.components.evm.transaction-builder
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount]]
            ["./contract.jsx" :as c]
            ["../../evm/abi.mjs" :as abi]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../Context.mjs" :refer [AppContext]]
            ["../blueprint/dropdown.jsx" :as d]
            ["../../transact.mjs" :as t]
            ["./transaction.jsx" :as tr]
            ["../../comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defn contract-select-on-change [{:keys [store setStore] :as ctx}]
  (let [m {"Codo" :codo
           "Codo Governor" :codo-governor}]
    (fn [e]
      (t/add-ident! ctx [:contract/id (get m e.target.value)] {:replace [:pages/id :transaction-builder :contract]}))))

(defc TransactionBuilder [this {:keys [contract {contracts [:contract/id :contract/name]}
                                       transactions]}]
  #jsx [:div {:class "grid grid-cols-1 md:(grid-cols-2 justify-center gap-0) 3xl:grid-cols-3 w-full h-full gap-4"}
        [:div {:class "grid col-span-full md:col-span-2 xl:col-span-1 dark:border-gray-600 border-gray-200 px-4 min-w-96 flex flex-col gap-3"}
         [:h1 {:class "mt-3 font-bold text-lg"} "Contract"]
         [d/dropdown-select {:title "Name"
                             :items #(mapv (fn [c] {:id (:contract/id c)
                                                    :value (:contract/name c)}) (contracts))
                             :on-change (contract-select-on-change ctx)
                             :selected #(second (contract))}]
         [Show {:when (contract)}
          [c/ui-contract (contract)]]]

        [:div {:class "col-span-full md:col-span-2 xl:col-span-1 h-full w-full overflow-y-auto items-top px-4"}
         [:h1 {:class "mt-3 font-bold text-lg"} "Transactions"]
         [:div {:class "dark:(placeholder-gray-400 focus:ring-blue-500 border-gray-700) w-full px-4
                          text-md overflow-auto"}
          [:div {:class "position-relative overflow-y-auto overflow-x-hidden"}
           [Show {:when #(not (empty? (transactions)))}
            [For {:each (transactions)}
             (fn [t _]
               #jsx [tr/ui-transaction t])]]]]]])

(def ui-transaction-builder (comp/comp-factory TransactionBuilder AppContext))


#_{:local/execute-fn (tr/execute-transaction ctx (contract) t)}
