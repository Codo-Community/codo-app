(ns components.blueprint.button)

(defn button [{:keys [title on-click color] :or {color "dark:bg-blue-600"}}]
  #jsx [:button {:class (str "text-white hover:bg-blue-800 focus:outline-none max-w-48 overflow-hidden
                            rounded-md text-sm py-3 border border-gray-300 dark:border-gray-800
                            dark:hover:bg-blue-700 h-11 " color)
                 :onClick on-click}
        [:span {:class "align-middle inline-block truncate px-3"} title]])

(defn icon-button [{:keys [icon on-click]}]
  #jsx [:button {:class (str "text-gray-400 bg-transparent truncate
                              hover:text-gray-900 text-sm h-10 w-10 block
                              inline-flex items-center dark:hover:text-white " icon)
                 :onClick on-click}])
