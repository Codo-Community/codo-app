(ns co-who.components.evm.transaction
  (:require ["solid-js" :refer [useContext createMemo]]
            ["./function.jsx" :as f]
            ["./inputs.jsx" :as ein]
            ["../blueprint/button.jsx" :as b]
            ["../blueprint/label.jsx" :as l]
            ["../../normad.mjs" :as n :refer [add]]
            ["../../evm/client.mjs" :as ec]
            ["../../evm/lib.mjs" :as el]
            ["../../comp.mjs" :as comp]
            ["../../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn execute-transaction [{:keys [store setStore] :as ctx} contract transaction]
  (fn [e]
    (let [{:contract/keys [abi address]} (n/pull store contract [:contract/address {:contract/abi [:name :type :stateMutability :inputs :outputs]}])
          {:keys [function/id type inputs name outputs stateMutability]} (:transaction/function (n/pull store transaction {:transaction/function [:function/id :type :inputs :name :outputs :stateMutability]}))
          c (el/get-contract {:address address
                              :abi abi
                              :client {:public ec/public-client :wallet @ec/wallet-client}})
          cf (if (= stateMutability "view") c.read c.write)
          function (aget cf name)
          args (mapv #(ein/convert-input-filter %) inputs)]
      (.then (function args)
             (fn [r]
               (if (vector? r)
                 (map-indexed (fn [i v] (ein/set-abi-field ctx (conj [:function/id id :outputs i :value] v))) r)
                 (ein/set-abi-field ctx [:function/id id :outputs 0 :value] r)))))))

(defn remove-evm-transaction [{:keys [store setStore] :as ctx} ident]
  (fn [e] (setStore :pages/id (fn [x]
                                (update-in x [:transaction-builder :transactions] #(filterv (fn [x] (not (= (second x)
                                                                                                 (second ident)))) %))))))


(defc Transaction [this {:transaction/keys [id function]}]
  #jsx [:div {:class "flex flex-col gap-3"}
        #_[:h2 {:class "mb-2 font-bold"} (str "id: ") [:text {:class ""} (:transaction/id (data))]]
        [l/label (str "id: " (id))]
        [f/ui-function (function)]
        [:span {:class "flex gap-3"}
         [b/button {:title "Transact"
                    :on-click #();execute-fn
                    }]
         [b/button {:title "Remove"
                    :on-click (remove-evm-transaction ctx [:transaction/id (id)])
                    :color "dark:bg-red-600 dark:hover:bg-red-700"}]]])

(def ui-transaction (comp/comp-factory Transaction AppContext))
