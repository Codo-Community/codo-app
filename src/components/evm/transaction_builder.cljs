(ns co-who.components.evm.transaction-builder
  (:require
   [clojure.string :as str]
   ["./contract.jsx" :as c]
   #_[",/inputs.jsx" :as in]
   ["../../evm/abi.mjs" :as abi]
   #_["./transactions.jsx" :as tr]
   #_[co-who.mutations :as m]
   #_[co-who.blueprint.input :as i]
   #_[co-who.blueprint.dropdown :as d]))

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

(defn TransactionBuilder []
  (let [a (println  "cg " (get-in contract-gen [:contract/id :codo :contract/abi]))]
    #jsx [:div {:class "flex flex-col grid grid-cols-1 md:grid-cols-2 md:justify-center 3xl:grid-cols-3 w-full h-full gap-4 md:gap-0"}
     [:div {:class "col-span-full md:col-span-1 3xl:col-span-1 flex flex-col dark:border-gray-600 border-gray-200 px-4"}
      [:h1 {:class "mb-3 font-bold text-lg"} "Contract"]
      #_(d/dropdown-select "Name" (mapv (fn [c] {:ident [:contract/id (:contract/id c)]
                                                 :value (:contract/name c)}) contracts) contract-select-on-change selected-contract "")
      [:div {}

       #_[Contract () contract]]]

     #_(dom/div {:class "col-span-full md:col-span-1 3xl:col-span-2 h-full w-full overflow-y-auto flex flex-col items-top"}
                (dom/h1 {:class "mb-3 font-bold text-lg"} "Transactions")
                (dom/div {:id :list
                          :class "dark:placeholder-gray-400 w-full dark:border-gray-700 px-4
                                    dark:text-white dark:focus:ring-blue-500 text-md overflow-auto"}
                         (dom/div {:class "position-relative overflow-y-auto overflow-x-hidden"}
                                  (for [t transaction-comps]
                                    ((second (tr/transaction-comp t {:local/execute-fn (tr/execute-transaction contract t)
                                                                     :local/remove-fn (tr/remove-evm-transaction t)})))))))]))
