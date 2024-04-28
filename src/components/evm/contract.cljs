(ns components.evm.contract)

(defn select-on-change [{:keys [store setStore] :as ctx} id]
  (setStore :counter/id
              (fn [counters]
                (update-in counters [id :counter/value] inc))))


(defn Contract [{:local/keys [selected-function]
                 :contract/keys [id address abi chain name] :as data}
                {:keys [on-change on-click] :or {on-change (i/on-change-fn co-who.app/app [:transaction-builder :input])
                                                 select-on-change (fn [e]
                                                                    (swap! co-who.app/app assoc-in [:contract/id id :local/selected-function] e.target.value))
                                                 on-click (tr/append-transaction co-who.app/app)}}]
  (let [select-on-change (fn [e]
                           (swap! co-who.app/app m/add-value {:contract/id id
                                                              :local/selected-function e.target.value}))]
    (list
     (fn [{:contract/keys [id address abi chain name]
           :keys [f local?] :or {id id address address abi abi chain chain name name
                                 f identity local? false}}] (if-not local?
                                                              (merge #:local{:selected-function selected-function}
                                                                     (f #:contract{:id id :address address :abi abi :chain chain :name name}))))
     (fn [{:local/keys [selected-function]
           :contract/keys [id address abi chain name] :as data}]
       [:div {:class "flex flex-col grid grid-cols-1 w-full gap-3"}
        [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
        [:span {:class "flex w-full gap-3"}
         #_[in/address-input {:name "Address"
                              :value address}]]
        [:div {:class "flex items-end"}
         (b/button {:title "Add"
                    :on-click on-click})]]))))
