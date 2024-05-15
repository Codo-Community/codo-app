(ns components.user
  (:require ["solid-js" :refer [createMemo useContext]]
            ["blo" :refer [blo]]
            ["../Context.mjs" :refer [AppContext]]))

(defn add-user [{:keys [store setStore] :as ctx} address]
  (let [id (count (:user/id store))]
    (setStore :user/id
              (fn [user-id]
                (assoc-in user-id [id] {:user/id id
                                        :user/ethereum-address address})))
    (setStore :header (fn [header]
                        (assoc header :user [:user/id id])))))


(defn User [ident]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo (fn []
                           (println "run memo user id " ident.children)
                           (get-in store ident.children)))]
    #jsx [:div {:class "flex flex-inline  items-center justify-items-center text-white"}
          [:img {:class "rounded-lg w-11 h-11 rounded-p md-0.5 text-gray-500 hover:bg-gray-100
                         focus:outline-none focus:ring-4 focus:ring-gray-200 dark:text-gray-400
                         dark:hover:bg-gray-700 dark:focus:ring-gray-700 select-none"
                 :draggable false
                 :onDragStart nil
                 :src (blo (:user/ethereum-address (data)))}]
          #_(Show show-name
                  [:div {:class "px-2"}]
                  (if-not (= first-name "")
                    (str first-name " " last-name)
                    (subs (str ethereum-address) 0 8)))]))
