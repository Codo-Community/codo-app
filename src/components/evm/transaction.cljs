(ns co-who.components.evm.transaction
  (:require [mr-who.dom :as dom]
            [co-who.components.evm.function :as f]
            [co-who.components.evm.inputs :as ein]
            [co-who.evm.client :as ec]
            [co-who.evm.lib :as el]))

(defn execute-transaction [{:contract/keys [abi address]}
                           transaction]
  (fn [e]
    (let [{:keys [function/id type inputs name outputs stateMutability]} (:transaction/function transaction)
          c (el/get-contract (clj->js {:address address
                                       :abi (clj->js abi)
                                       :client (clj->js {:public @ec/public-client :wallet @ec/wallet-client})}))
          cf (if (= stateMutability "view") c.read c.write)
          function (aget cf name)
          args (mapv #(ein/convert-input-filter %) inputs)]
      (.then (function (clj->js args)) #(println %)))))

(defn transaction [{:transaction/keys [id function] :as data :or {id (random-uuid)
                                                                  function nil}} {:local/keys [execute-fn remove-fn open?] :or {open? true} :as local}]
  (list (fn [] #:transaction{:id id :function function})
        (fn [] (dom/div {:class "flex flex-col mb-4"}
                 (f/abi-entry function {:local/on-change-id [:transaction/id id]})
                 (dom/span {:class "flex gap-3"}
                   (b/button "Transact" execute-fn)
                   (b/button "Remove" remove-fn {:color "dark:bg-red-600 dark:hover:bg-red-700"}))))
        [:transaction/id {:transaction/function [:function/id :name {:inputs [:internalType :type :name :value]}
                                                 {:outputs [:internalType :type :name :value]}
                                                 :stateMutability :type]}]))

(defn append-transaction [app]
  (fn [e]
    (let [selected (get-in @app [:id :transaction-builder :local/selected-function])
          ident [:contract/id (keyword (clojure.string/replace (clojure.string/lower-case (get-in @app [:id :transaction-builder :local/selected-contract])) " " "-"))]
          contract (get-in @co-who.app/app ident)
          function-data (get-in @app [:function/id selected])
          transaction-data {:transaction/id (random-uuid)
                            :transaction/function (assoc-in function-data [:function/id] (random-uuid))}
          tr (transaction transaction-data {:local/execute-fn (partial execute-transaction contract)})
          ]
      (swap! app py/add ((first tr)))
      (swap! app update-in [:id :transaction-builder :transactions] conj [:transaction/id (:transaction/id transaction-data)]))))

(defn remove-evm-transaction [transaction]
  (fn []
    (swap! co-who.app/app py/delete [:transaction/id (:transaction/id transaction)])))
