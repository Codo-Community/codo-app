(ns co-who.components.evm.transaction
  (:require ["solid-js" :refer [createSignal Show]]
            ["./function.cljs" :as f]
            ["./input.cljs" :as ein]
            ["../blueprint/button.cljs" :as b]
            ["../blueprint/label.cljs" :as l]
            ["../../evm/client.cljs" :as ec]
            ["../../evm/lib.cljs" :as el]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defn execute-transaction [{:keys [store setStore] :as ctx} contract transaction]
  (fn [e]
    (let [{:contract/keys [abi address]} (sqeave/pull store (contract) [:contract/address {:contract/abi [:name :type :stateMutability :inputs :outputs]}])
          {:keys [function/id type inputs name outputs stateMutability]} (:transaction/function (sqeave/pull store transaction {:transaction/function [:function/id :type :inputs :name :outputs :stateMutability]}))
          a (println abi)
          c (el/get-contract {:address address
                              :abi abi
                              :client {:public @ec/public-client :wallet @ec/wallet-client}})
          cf (if (= stateMutability "view") c.read c.write)
          a (println "cf: " {:public @ec/public-client :wallet @ec/wallet-client}  )
          function (aget cf name)
          args (mapv #(ein/convert-input-filter %) inputs)
          a (println "f:" function "a: " args)]
      (.then (function args)
             (fn [r]
               (println "r: " r)
               (if (vector? r)
                 (map-indexed (fn [i v] (ein/set-abi-field ctx (conj [:function/id id :outputs i :value] v))) r)
                 (ein/set-abi-field ctx [:function/id id :outputs 0 :value] r)))))))

(defn remove-evm-transaction [{:keys [store setStore] :as ctx} ident]
  (fn [e] (setStore :component/id (fn [x]
                                (update-in x [:transaction-builder :transactions] #(filterv (fn [x] (not (= (second x)
                                                                                                 (second ident)))) %))))))

(defc Transaction [this {:transaction/keys [id function]}]
  (let [[open? setOpen] (createSignal true)]
    #jsx [:div {:class "flex flex-col gap-3 mb-6"}
          [:span {:class "w-full border-b border-gray-600 flex items-center gap-3"}
           [:button {:class (str "dark:text-gray-400 w-5 h-5 relative top-0 " (if (open?) "i-tabler-chevron-down" "i-tabler-chevron-right"))
                     :onClick #(setOpen (not (open?)))}]
           [l/label {:title (id)}]]
          [Show {:when (open?)}
           [:div {}
            [f/Function {:& {:ident (function)}}]
            [:span {:class "flex gap-3 mt-2"}
             [b/button {:title "Transact"
                        :on-click (execute-transaction ctx (:contract props) [:transaction/id (id)])}]
             [b/button {:title "Remove"
                        :on-click (remove-evm-transaction ctx [:transaction/id (id)])
                        :extra-class "!dark:(ring-red-500 text-red-500 border-red-500)"}]]]]]))
