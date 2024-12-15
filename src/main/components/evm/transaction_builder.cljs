(ns co-who.components.evm.transaction-builder
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount]]
            ["./contract.cljs" :as c]
            ["../../evm/abi.cljs" :as abi]
            ["../blueprint/dropdown.cljs" :as d]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/split.cljs" :as s]
            ["./transaction.cljs" :as tr]
            ["flowbite" :as fb]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defn contract-select-on-change [{:keys [store setStore] :as ctx}]
  (let [m {"Codo" :codo
           "Codo Governor" :codo-governor}]
    (fn [e]
      (sqeave/add-ident! ctx [:contract/id (get m e.target.value)] {:replace [:component/id :transaction-builder :contract]}))))

(defc TransactionBuilder [this {:keys [component/id
                                       contract {contracts [:contract/id :contract/name {:contract/chain [:chain/id :chain/name :chain/logo]}]}
                                       transactions]
                                :or {component/id :transaction-builder
                                     contracts [{:contract/id :codo :contract/name "Codo"
                                                 :contract/address "0x1Ed3c3b6DFb4756c03CCBCBC5407E73682B6E3Db" :contract/chain {:chain/id 11155111 :chain/name "Sepolia" :chain/logo "/images/chain-logos/ethereum-eth-logo.svg"}
                                                 :contract/abi (abi/indexed-abi abi/token-abi)}
                                                {:contract/id :codo-governor :contract/name "Codo Governor"
                                                 :contract/address "0x0d4d1e9665a8BF75869A63e3F45AC465Bc291CBB" :contract/chain [:chain/id 11155111]
                                                 :contract/abi (abi/indexed-abi abi/governor-abi)}]
                                     contract [:contract/id :codo]
                                     transactions []}}]
  (do
    (onMount #(fb/initFlowbite))
    #jsx [s/Split {}
          [s/SplitItem {:& {:extra-class "md:border-r"}}
           [:h1 {:class "mt-3 font-bold text-lg"} "Contract"]
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
          [s/SplitItem {}
           [:h1 {:class "my-3 font-bold text-lg"} "Transactions"]
           [:div {:class "dark:(placeholder-gray-400 focus:ring-blue-500 border-gray-700) w-full px-4
                          text-md overflow-auto"}
            [:div {:class "position-relative overflow-y-auto overflow-x-hidden"}
             [Show {:when (not (empty? (transactions)))}
              [For {:each (transactions)}
               (fn [t _]
                 #jsx [tr/Transaction {:& {:ident t
                                           :contract contract}}])]]]]]]))


#_{:local/execute-fn (tr/execute-transaction ctx (contract) t)}
