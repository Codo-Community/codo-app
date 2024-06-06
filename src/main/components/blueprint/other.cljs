(ns main.components.blueprint.other)

(defn drop-down []
  #jsx [:div {}
        [:button {:class "text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center inline-flex items-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
                  :type "button"
                  :id "dropdownDefaultButton"
                  :data-dropdown-toggle "dropdown"} "Doo"]
        [:div {:id "dropdown"
               :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow w-44 dark:bg-gray-700"}
         [:ul {:class "py-2 text-sm text-gray-700 dark:text-gray-200" :aria-labelledby "dropdownDefaultButton"}
          [:li {} [:a {:href "#"} "Dasboasd"]]]]])
