(ns components.evm.contract)

#_(defn select-on-change [{:keys [store setStore] :as ctx} id]
  (setStore :counter/id
              (fn [counters]
                (update-in counters [id :counter/value] inc))))

(defn Contract []
  (let []
    #jsx [:div {:class "flex flex-col grid grid-cols-1 w-full gap-3"}
     [:h2 {:class "py-3 font-bold text-lg"} "Parameters"]
     [:span {:class "flex w-full gap-3"}
      #_[in/address-input {:name "Address"
                           :value address}]]
     [:div {:class "flex items-end"}
      #_(b/button {:title "Add"
                   :on-click on-click})]]))
