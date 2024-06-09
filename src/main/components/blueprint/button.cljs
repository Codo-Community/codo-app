(ns components.blueprint.button)

(defn button [{:keys [title on-click color] :or {color "dark:bg-blue-600"}}]
  #jsx [:button {:class (str "text-white hover:bg-blue-800 focus:outline-none
                            rounded-md text-sm px-3 py-3 border border-gray-300 dark:border-gray-600
                            dark:hover:bg-blue-700 h-11 " color)
                 :onClick on-click}
        [:span {:class "align-middle inline-block"} title]])

(defn icon-button [{:keys [icon]}]
  #jsx [:button {:class "text-gray-400 bg-transparent
                         hover:text-gray-900 text-sm h-10 w-10 block
                         inline-flex items-center dark:hover:text-white "}
        icon
        #_[:div {:class (str "w-6 h-6 " icon)}]])
