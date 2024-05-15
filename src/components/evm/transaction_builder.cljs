(ns co-who.components.evm.transaction-builder
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index]]
            [clojure.string :as str]
            ["./contract.jsx" :as c]
            ["../../evm/abi.mjs" :as abi]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../Context.mjs" :refer [AppContext]]
            ["../blueprint/dropdown.jsx" :as d]
            ["./transaction.jsx" :as tr]))

;; simulate a remote
(def contract-gen
  {:contract/id {:codo {:contract/id :codo :contract/name "Codo"
                        :contract/address "0x1Ed3c3b6DFb4756c03CCBCBC5407E73682B6E3Db" :contract/chain :sepolia
                        :contract/abi (abi/indexed-abi abi/token-abi)}
                 :codo-governor {:contract/id :codo-governor :contract/name "Codo Governor"
                                 :contract/address "0x0d4d1e9665a8BF75869A63e3F45AC465Bc291CBB" :contract/chain :sepolia
                                 :contract/abi (abi/indexed-abi abi/governor-abi)}}})

#_(defn get-or-create [ident component]
    (if (get-in co-who.app/app ident)
      (let [data (py/pull @co-who.app/app ident)]
        (m/update-children! co-who.app/app [:id :transaction-builder :contract] ident))
      (do
        (println "replace")
        (swap! co-who.app/app py/add (get-in contract-gen ident))

        (m/merge-component! co-who.app/app (c/contract-comp {} {}) {:target [{[:id :transaction-builder] [{:contract [:mr-who/node]}]}]
                                                                    :action :replace-children}))))

(defn contract-select-on-change [{:keys [store setStore] :as ctx}]
  (let [m {"Codo" :codo
           "Codo Governor" :codo-governor}]
    (fn [e]
      (setStore :transaction-builder
                (fn [x]
                  (assoc-in x [:contract] [:contract/id (get m e.target.value)]))))))

(defn TransactionBuilder []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        norm (do (println "now run long running norm add")
                 (add ctx contract-gen))
        data (createMemo (fn []
                           (n/pull store (get store :transaction-builder)
                                   [:contract {:contracts [:contract/id :contract/name]}
                                    :transactions])))]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 md:(grid-cols-2 justify-center gap-0) 3xl:grid-cols-3 w-full h-full gap-4"}
          [:div {:class "col-span-full md:col-span-1 3xl:col-span-1 flex flex-col dark:border-gray-600 border-gray-200 px-4"}
           [:h1 {:class "mb-3 font-bold text-lg"} "Contract"]
           (d/dropdown-select "Name" (mapv (fn [c] {:id (:contract/id c)
                                                    :value (:contract/name c)}) (:contracts (data)))
                              (contract-select-on-change ctx) (second (:contract (data))))
           #jsx [c/Contract (:contract (data))]]

          [:div {:class "col-span-full md:col-span-1 3xl:col-span-2 h-full w-full overflow-y-auto flex flex-col items-top"}
           [:h1 {:class "mb-3 font-bold text-lg"} "Transactions"]
           [:div {:class "dark:(placeholder-gray-400 text-green focus:ring-blue-500 border-gray-700) w-full px-4
                          text-md overflow-auto"}
            [:div {:class "position-relative overflow-y-auto overflow-x-hidden"}
             #jsx [For {:each (:transactions (data))}
                   (fn [t _]
                     #jsx [tr/Transaction t #_{:local/execute-fn (tr/execute-transaction (:contract (data)) t)
                                               :local/remove-fn (tr/remove-evm-transaction t)}])]]]]]))
