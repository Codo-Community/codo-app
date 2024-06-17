(ns components.blueprint.dropdown
  (:require ["solid-js" :refer [For Show]]
            ["./label.cljs" :as l]))

(defn dropdown-select [{:keys [id title items on-change selected extra-class]}]
  #jsx [:div {:id id
              :class (str " " extra-class)}
        [Show {:when (not (nil? title))}
         (l/label title)]
        [:select {:onChange on-change
                  :class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500 p-3 pr-6
                          focus:border-blue-500 block h-11 w-fit dark:bg-black dark:border-gray-600 dark:placeholder-gray-400
                          dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500 cursor-pointer "}
         #jsx [For {:each (items)}
               (fn [{:keys [value icon href id] :as item} i]
                 [:option {:selected (if (= id (selected))
                                       true false)}
                  value])]]])

(defn dropdown [{:keys [id title items on-change selected extra-class]}]
  #jsx [:div {:id id
              :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow w-44 dark:bg-black dark:divide-gray-600 border-1 dark:border-gray-600"}
        [:div {:class "px-4 py-3 text-sm text-gray-900 dark:text-white"}
         [:div {:class "font-medium"} "Chains"]]
        [:ul {:class "py-2 text-sm text-gray-700 dark:text-gray-200"}
         #jsx [For {:each (items)}
               (fn [{:keys [value icon href id] :as item} i]
                 #jsx [:li {:class ""}
                       [:button {:class "w-full flex gap-2 block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white items-center"}
                        [Show {:when icon}
                         [:img {:class "w-8 h-8 p-1 flex items-center justify-center"
                                :draggable false
                                :onDragStart nil
                                :src icon}]]
                        [:a {:class ""
                             :onClick on-change}
                         value]]])]]])
