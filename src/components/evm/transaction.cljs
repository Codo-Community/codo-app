(ns co-who.components.evm.transaction
  (:require ["solid-js" :refer [useContext createMemo]]
            ["./function.jsx" :as f]
            ["./inputs.jsx" :as ein]
            ["../blueprint/button.jsx" :as b]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../evm/client.mjs" :as ec]
            ["../../evm/lib.mjs" :as el]
            ["../../Context.mjs" :refer [AppContext]]))

(defn execute-transaction [{:contract/keys [abi address]}
                           transaction]
  (fn [e]
    (let [{:keys [function/id type inputs name outputs stateMutability]} (:transaction/function transaction)
          c (el/get-contract {:address address
                              :abi abi
                              :client {:public @ec/public-client :wallet @ec/wallet-client}})
          cf (if (= stateMutability "view") c.read c.write)
          function (aget cf name)
          args (mapv #(ein/convert-input-filter %) inputs)]
      (.then (function args) #(println %)))))

(defn remove-evm-transaction [{:keys [store setStore] :as ctx} ident]
  (fn []
    (setStore :transaction-builder (fn [x]
                                     (println (update-in x [:transactions] #(filterv (fn [x] (not (= (second x)
                                                                                                     (second ident)))) %)))
                                     (update-in x [:transactions] #(filterv (fn [x] (not (= (second x)
                                                                                            (second ident)))) %))
                                     ))))


(defn Transaction [ident {:local/keys [execute-fn open?] :or {open? true} :as local}]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        query [:transaction/id {:transaction/function [:function/id :name
                                                       {:inputs [:internalType :type :name :value]}
                                                       {:outputs [:internalType :type :name :value]}
                                                       :stateMutability :type]}]
        data (createMemo #(n/pull store (get-in store ident.children) query))]
    #jsx [:div {:class "flex flex-col mb-4"}
          #_(str "id " (:transaction/id (data)))
          (f/abi-entry (get-in (data) [:transaction/function])
                       {:local/on-change [:transaction/id (:transaction/id (data))]})
          [:span {:class "flex gap-3"}
           (b/button "Transact" execute-fn)
           (b/button "Remove" (remove-evm-transaction ctx ident.children) {:color "dark:bg-red-600 dark:hover:bg-red-700"})]]))

#_(defn append-transaction [app]
  (fn [e]
    (let [selected (get-in @app [:id :transaction-builder :local/selected-function])
          ident [:contract/id (keyword (clojure.string/replace (clojure.string/lower-case (get-in @app [:id :transaction-builder :local/selected-contract])) " " "-"))]
          contract (get-in @co-who.app/app ident)
          function-data (get-in @app [:function/id selected])
          transaction-data {:transaction/id (random-uuid)
                            :transaction/function (assoc-in function-data [:function/id] (random-uuid))}
          tr (Transaction transaction-data {:local/execute-fn (partial execute-transaction contract)})
          ]
      (swap! app py/add ((first tr)))
      (swap! app update-in [:id :transaction-builder :transactions] conj [:transaction/id (:transaction/id transaction-data)]))))
