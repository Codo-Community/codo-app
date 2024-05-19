(ns co-who.components.evm.transaction
  (:require ["solid-js" :refer [useContext createMemo]]
            ["./function.jsx" :as f]
            ["./inputs.jsx" :as ein]
            ["../blueprint/button.jsx" :as b]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../evm/client.mjs" :as ec]
            ["../../evm/lib.mjs" :as el]
            ["../../Context.mjs" :refer [AppContext]]))

(defn execute-transaction [{:keys [store setStore] :as ctx} contract transaction]
  (fn [e]
    (let [{:contract/keys [abi address]} (n/pull store contract [:contract/address {:contract/abi [:name :type :stateMutability :inputs :outputs]}])
          {:keys [function/id type inputs name outputs stateMutability]} (:transaction/function (n/pull store transaction {:transaction/function [:function/id :type :inputs :name :outputs :stateMutability]}))
          c (el/get-contract {:address address
                              :abi abi
                              :client {:public ec/public-client :wallet ec/wallet-client}})
          cf (if (= stateMutability "view") c.read c.write)
          function (aget cf name)
          args (mapv #(ein/convert-input-filter %) inputs)]
      (.then (function args)
             (fn [r]
               (if (vector? r)
                 (map-indexed (fn [i v] (ein/set-abi-field ctx (conj [:function/id id :outputs i :value] v))) r)
                 (ein/set-abi-field ctx [:function/id id :outputs 0 :value] r)))))))

(defn remove-evm-transaction [{:keys [store setStore] :as ctx} ident]
  (fn [] (setStore :transaction-builder (fn [x]
                                          (update-in x [:transactions] #(filterv (fn [x] (not (= (second x)
                                                                                                 (second ident)))) %))))))


(defn Transaction [ident {:local/keys [execute-fn open?] :or {open? true} :as local}]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        query [:transaction/id :transaction/function]
        data (createMemo (fn [] (n/pull store (get-in store ident) query)))]
    #jsx [:div {:class "flex flex-col mb-4"}
          (f/function (:transaction/function (data)))
          [:span {:class "flex gap-3"}
           (b/button "Transact" execute-fn)
           (b/button "Remove" (remove-evm-transaction ctx ident) {:color "dark:bg-red-600 dark:hover:bg-red-700"})]]))

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
