(ns components.pages.transaction-builder
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../components/evm/transaction_builder.cljs" :as tb]
            ["../evm/abi.cljs" :as abi]
            ["flowbite" :as fb]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

;; {:chain/id 11155111 :chain/name "Sepolia" :chain/logo "/images/chain-logos/ethereum-eth-logo.svg"}

;; simulate a remote
(def contract-gen
  {:contract/id {:codo {:contract/id :codo :contract/name "Codo"
                        :contract/address "0xAb66897b828b051C124c78B902a335619EA96D65" :contract/chain {:chain/id 137 :chain/name "Polygon" :chain/logo "/images/chain-logos/polygon-matic-logo.svg"}
                        :contract/abi (abi/indexed-abi abi/token-abi)}
                 :codo-governor {:contract/id :codo-governor :contract/name "Codo Governor"
                                 :contract/address "0x7531eD82Cf38b7d9C650a9EC1a8684B53eAbfCE3" :contract/chain [:chain/id 137]
                                 :contract/abi (abi/indexed-abi abi/governor-abi)}}})

(defc TransactionBuilderPage [this {:keys [] :or {pages/id :transaction-builder
                                                  contracts [[:contract/id :codo] [:contract/id :codo-governor]]
                                                  contract [:contract/id :codo]
                                                  transactions []}}]
  (do
    (onMount #(do (println "long add")
                  (sqeave/add! ctx contract-gen)
                  (fb/initFlowbite)))
    #jsx [tb/TransactionBuilder {:& {:ident [:pages/id :transaction-builder]}}]))
