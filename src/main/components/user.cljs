(ns components.user
  (:require ["blo" :refer [blo]]
            ["./blueprint/tooltip.jsx" :as tt]
            ["../comp.mjs" :as comp]
            ["../transact.mjs" :as t]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn add-user [{:keys [store setStore] :as ctx} address & extra]
  #_(println (first extra))
  (t/add! ctx {:user/id (count (:user/id store))
               :user/ethereum-address address} (first extra) #_{:replace [:header :user]}))

(defc User [this {:user/keys [id ethereum-address]}]
  #jsx [:img {:class "rounded-md w-10 h-10 md-0.5 text-gray-500 hover:bg-gray-100
                       focus:outline-none focus:ring-4 focus:ring-gray-200 dark:text-gray-400
                       dark:hover:bg-gray-700 dark:focus:ring-gray-700 select-none"
              :draggable false
              :onDragStart nil
              :src (blo (ethereum-address))}
        #_(Show show-name
                [:div {:class "px-2"}]
                (if-not (= first-name "")
                  (str first-name " " last-name)
                  (subs (str ethereum-address) 0 8)))]
  #_[tt/tooltip {:id "header-user-tt" :content ethereum-address}])

(def ui-user (comp/comp-factory User AppContext))
