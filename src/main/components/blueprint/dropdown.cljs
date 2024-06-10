(ns components.blueprint.dropdown
  (:require ["solid-js" :refer [For Show]]
            ["./label.cljs" :as l]))

(defn dropdown-select [{:keys [title items on-change selected]}; & {:keys [extra-class] :or {extra-class nil}}
                       ]
  #jsx [:div {:class (str " w-full " #_(first extra-class))}
        [Show {:when (not (nil? title))}
         (l/label title)]
        [:select {:onChange on-change
                  :class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500
                          focus:border-blue-500 block h-11 w-full p-3 dark:bg-black dark:border-gray-600 dark:placeholder-gray-400
                          dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500 cursor-pointer"}
         #jsx [For {:each (items)}
               (fn [{:keys [value href id] :as item} i]
                 #jsx [:option {:selected (if (= id (selected))
                                            true false)}
                       value])]]])

(defn dropdown [{:keys [id items]}]
  #jsx [:div {:id id
              :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow w-44 dark:bg-gray-700 dark:divide-gray-600"}
        [:div {:class "px-4 py-3 text-sm text-gray-900 dark:text-white"}
         [:div {:class "font-medium"} "Pro User"]
         [:div {:class "truncate"} "name@flowbite.com"]]
        [:ul {:class "py-2 text-sm text-gray-700 dark:text-gray-200"
              :aria-labelledby "dropdownInformdropdownAvatarNameButtonationButton"}
         [For {:each items}
          [:li
           [:a {:href "#"
                :class "block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white"}
            "Dashboard"]]]
         ]
        [:div {:class "py-2"}
         [:a {:href "#"
              :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 dark:text-gray-200 dark:hover:text-white"}
          "Sign out"]]])
