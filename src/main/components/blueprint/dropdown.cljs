(ns components.blueprint.dropdown
  (:require ["solid-js" :refer [For Show]]
            ["./label.jsx" :as l]))

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
