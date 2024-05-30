(ns components.pages.transaction-builder
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../components/evm/transaction_builder.jsx" :as tb]
            ["../evm/abi.mjs" :as abi]
            ["../transact.mjs" :as t]
            ["../Context.mjs" :refer [AppContext]]))

;; simulate a remote
(def contract-gen
  {:contract/id {:codo {:contract/id :codo :contract/name "Codo"
                        :contract/address "0x1Ed3c3b6DFb4756c03CCBCBC5407E73682B6E3Db" :contract/chain :sepolia
                        :contract/abi (abi/indexed-abi abi/token-abi)}
                 :codo-governor {:contract/id :codo-governor :contract/name "Codo Governor"
                                 :contract/address "0x0d4d1e9665a8BF75869A63e3F45AC465Bc291CBB" :contract/chain :sepolia
                                 :contract/abi (abi/indexed-abi abi/governor-abi)}}})

(defn TransactionBuilderPage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)]
    (onMount #(do (println "long add")
                  (t/add! ctx contract-gen)
                  (t/set-field! ctx [:pages/id :transaction-builder :contract] [:contract/id :codo])
                  (t/set-field! ctx [:pages/id :transaction-builder :contracts] [[:contract/id :codo] [:contract/id :codo-governor]])))
    (let [ident [:pages/id :transaction-builder]]
      #jsx [:div {}
            [tb/ui-transaction-builder ident]])))

(def default TransactionBuilderPage)
