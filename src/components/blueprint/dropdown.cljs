(ns components.blueprint.dropdown
  (:require ["./label.jsx" :as l]))

(defn dropdown-select [title items on-change selected & {:keys [extra-class] :or {extra-class nil}}]
  #jsx [:div {:class (str " w-full " (first extra-class))}
        (l/label title)
        [:select {:onChange on-change
                  :class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500
                     focus:border-blue-500 block h-11 w-full p-3 dark:bg-black dark:border-gray-600 dark:placeholder-gray-400
                     dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500 cursor-pointer"}
         (map-indexed (fn [i {:keys [value href id] :as item}]
                        #jsx [:option (if (= id selected)
                                        {:selected true} {})
                              [:a {:href href} value]]) items)]])
