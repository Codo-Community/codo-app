(ns components.user
  (:require ["blo" :refer [blo]]
            ["../comp.mjs" :as comp]
            ["../transact.mjs" :as t]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn add-user [{:keys [store setStore] :as ctx} address & extra]
  (println (first extra))
  (t/add! ctx {:user/id (count (:user/id store))
               :user/ethereum-address address} {:replace [:header :user]})
  #_(setStore :user/id
              (fn [user-id]
                (assoc-in user-id [id] {:user/id id
                                        :user/ethereum-address address})))
  #_(setStore :header (fn [header]
                        (assoc header :user [:user/id id]))))

(defc User [this {:user/keys [id ethereum-address]}]
  #jsx [:div {:class "flex flex-inline  items-caenter justify-items-center text-white"}
        [:img {:class "rounded-lg w-11 h-11 rounded-p md-0.5 text-gray-500 hover:bg-gray-100
                       focus:outline-none focus:ring-4 focus:ring-gray-200 dark:text-gray-400
                       dark:hover:bg-gray-700 dark:focus:ring-gray-700 select-none"
               :draggable false
               :onDragStart nil
               :src (blo (ethereum-address))}
         #_ (Show show-name
                  [:div {:class "px-2"}]
                  (if-not (= first-name "")
                    (str first-name " " last-name)
                    (subs (str ethereum-address) 0 8)))]])

(def ui-user (comp/comp-factory User AppContext))

#_(println "render: " ui-user #_(let [c (new User AppContext)]
                      (aget c "render")))
