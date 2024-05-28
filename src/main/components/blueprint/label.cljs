(ns components.blueprint.label)

(defn label [title & extra]
  #jsx [:label {:class (str "block mb-2 text-sm font-medium text-gray-900 dark:text-white " (if (or (= "" title) (nil? title)) "hidden ") (first extra))}
   title])
