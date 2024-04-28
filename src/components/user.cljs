(ns components.user
  (:require ["solid-js" :refer [createMemo]]
            ["blo" :refer [blo]]))

(defn User [{:keys [ctx ident]}]
  (let [{:keys [store setStore]} ctx
        data (createMemo (if ident #(get-in store ident) (fn [] #:user{:id -1
                                                                       :ethereum-address "0x0"})))]
    #jsx [:div {:class "flex flex-inline  items-center justify-items-center"}
          [:img {:class "rounded-lg w-11 h-11 rounded-md p-0.5 text-gray-500 hover:bg-gray-100
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
