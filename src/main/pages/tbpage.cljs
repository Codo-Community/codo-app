(ns components.pages.transaction-builder
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../components/evm/transaction_builder.cljs" :as tb]
            ["../evm/abi.cljs" :as abi]
            ["../transact.cljs" :as t]
            ["flowbite" :as fb]
            ["../Context.cljs" :refer [AppContext]]))

;; simulate a remote
(def contract-gen
  {:contract/id {:codo {:contract/id :codo :contract/name "Codo"
                        :contract/address "0x1Ed3c3b6DFb4756c03CCBCBC5407E73682B6E3Db" :contract/chain {:chain/id 11155111 :chain/name "Sepolia" :chain/logo "/images/chain-logos/ethereum-eth-logo.svg"}
                        :contract/abi (abi/indexed-abi abi/token-abi)}
                 :codo-governor {:contract/id :codo-governor :contract/name "Codo Governor"
                                 :contract/address "0x0d4d1e9665a8BF75869A63e3F45AC465Bc291CBB" :contract/chain [:chain/id 11155111]
                                 :contract/abi (abi/indexed-abi abi/governor-abi)}}})

(defn TransactionBuilderPage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)]
    (onMount #(do (println "long add")
                  (t/add! ctx contract-gen)
                  (t/set-field! ctx [:pages/id :transaction-builder :contract] [:contract/id :codo])
                  (t/set-field! ctx [:pages/id :transaction-builder :contracts] [[:contract/id :codo] [:contract/id :codo-governor]])
                  (fb/initFlowbite)))
    #jsx [tb/ui-transaction-builder {:& {:ident [:pages/id :transaction-builder]}}]))

(def default TransactionBuilderPage)
