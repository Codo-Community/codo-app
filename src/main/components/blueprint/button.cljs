(ns components.blueprint.button
  (:requre ["solid-js" :refer [Show]]))

(defn button [{:keys [title on-click color icon extra-class] :or {color "dark:bg-black"}}]
  #jsx [:button {:class (str "text-white focus:outline-none max-w-48 overflow-hidden
                            rounded-md text-sm p-2 border border-2 border-gray-400 dark:border-zinc-800
                            h-fit w-fit " color " " extra-class)
                 :onClick on-click}
        [:span {:class "flex align-middle inline-block truncate px-3"}
         [Show {:when icon}
          [:div {:class (str " " icon)}]]
         title]])
