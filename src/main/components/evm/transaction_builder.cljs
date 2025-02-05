(ns co-who.components.evm.transaction-builder
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount]]
            ["./contract.cljs" :as c]
            ["../../evm/abi.cljs" :as abi]
            ["../blueprint/dropdown.cljs" :as d]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/split.cljs" :as s]
            ["./transaction.cljs" :as tr]
            ["flowbite" :as fb]
            ["viem" :as viem]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defn contract-select-on-change [{:keys [store setStore] :as ctx}]
  (let [m {"Codo" :codo
           "Codo Governor" :codo-governor
           "Timelock" :timelock}]
    (fn [e]
      (sqeave/add-ident! ctx [:contract/id (get m e.target.value)] {:replace [:component/id :transaction-builder :contract]}))))

(def token-x-address "0xAb66897b828b051C124c78B902a335619EA96D65")   ;; Replace with actual Token X contract address
(def recipient "0xa8172E99effDA57900e09150f37Fea5860b806B4")               ;; Replace with actual recipient address
(def amount (js/BigInt (* 400 (Math/pow 10 18)))) ;; 400 tokens (assuming 18 decimals)


(def calldata
  (viem.encodeFunctionData
   {:abi [{"name" "transfer"
           "type" "function"
           "stateMutability" "nonpayable"
           "inputs" [{"name" "recipient" "type" "address"}
                     {"name" "amount" "type" "uint256"}]
           "outputs" [{"type" "bool"}]}]
    :functionName "transfer"
    :args [recipient amount]}))

(def propose-args
  [{:internalType "address[]" :type "address[]" :value [token-x-address]}
   {:internalType "uint256[]" :type "uint256[]" :value [0]} ;; No ETH sent
   {:internalType "bytes[]" :type "bytes[]" :value [calldata]}
   {:internalType "string" :type "string" :value "Proposal: Transfer 400 Token X to Account Y"}])

(defc TransactionBuilder [this {:keys [component/id
                                       contract {contracts [:contract/id :contract/name {:contract/chain [:chain/id :chain/name :chain/logo]}]}
                                       transactions]
                                :or {component/id :transaction-builder
                                     contracts [{:contract/id :codo :contract/name "Codo"
                                                 :contract/address "0xAb66897b828b051C124c78B902a335619EA96D65" :contract/chain {:chain/id 137 :chain/name "Polygon" :chain/logo "/images/chain-logos/polygon-matic-logo.svg"}
                                                 :contract/abi (abi/indexed-abi abi/token-abi)}
                                                {:contract/id :codo-governor :contract/name "Codo Governor"
                                                 :contract/address "0x7531eD82Cf38b7d9C650a9EC1a8684B53eAbfCE3" :contract/chain [:chain/id 137]
                                                 :contract/abi (abi/indexed-abi abi/governor-abi)}
                                                {:contract/id :timelock :contract/name "Timelock"
                                                 :contract/address "0xbCE07D957fC4D37DE55a209d1EA5952A10CF789e" :contract/chain [:chain/id 137]
                                                 :contract/abi (abi/indexed-abi abi/timelock-abi)}
                                                ]
                                     contract [:contract/id :codo]
                                     transactions []}}]
  (do
    (onMount #(fb/initFlowbite))
    #jsx [s/Split {:& {:extra-class "max-h-full"}}
          [s/SplitItem {:& {:extra-class "md:border-r max-h-full pr-4"}}
           [:h1 {:class "font-bold text-lg"} "Contract"]
           [:span {:class "flex gap-6 flex-row w-full"}
            [d/dropdown-select {:& {:title "Name"
                                    :items #(mapv (fn [c] {:id (:contract/id c)
                                                           :value (:contract/name c)}) (contracts))
                                    :on-change (contract-select-on-change ctx)
                                    :selected #(second (contract))}}]
            [:div {:class "flex flex-col gap-1"}
             [l/label {:title "Chain"}]
             (let [{:chain/keys [id name logo]} (:contract/chain (get (contracts) (.indexOf (mapv #(:contract/id %) (contracts)) (second (contract)))))]
               #jsx [:div {:class "flex items-center justify-center text-lg"}
                     [:img {:class "w-8 h-8 p-1"
                            :draggable false
                            :onDragStart nil
                            :src logo}]
                     name])]]
           [Show {:when (contract)}
            [c/Contract {:& {:ident contract}}]]]
          [s/SplitItem {:& {:extra-class "max-h-full overflow-y-scroll"}}
           [:h1 {:class "font-bold text-lg"} "Transactions"]
           [:div {:class "dark:(placeholder-gray-400 focus:ring-blue-500 border-gray-700) w-full px-4
                          text-md h-full overflow-y-scroll overflow-x-hidden pr-4"}
            [Show {:when (not (empty? (transactions))) :fallback (fn [] #jsx [:p {} "No transactions added."])}
             [For {:each (transactions)}
              (fn [t _]
                #jsx [tr/Transaction {:& {:ident t
                                          :contract contract}}])]]]]]))


#_{:local/execute-fn (tr/execute-transaction ctx (contract) t)}
>
