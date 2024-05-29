(ns components.blueprint.button)

(defn button [{:keys [title on-click color] :or {color "dark:bg-blue-600"}}]
  #jsx [:button {:class (str "text-white hover:bg-blue-800 focus:outline-none
                            rounded-md text-sm px-3 py-3 border border-gray-300 dark:border-gray-600
                            dark:hover:bg-blue-700 h-11 " color)
                 :onClick on-click}
        [:span {:class "align-middle inline-block"} title]])

(defn icon-button [{:keys [icon]}]
  #jsx [:button {:class "text-gray-400 bg-transparent
                         hover:text-gray-900 rounded-md text-sm p-1.5 block h-10 w-10
                         ml-auto inline-flex items-center dark:hover:text-white "}
        [:div {:class "w-6 h-6"} #jsx [icon]]])
