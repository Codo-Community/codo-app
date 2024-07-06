(ns components.blueprint.label)

(defn label [{:keys [title extra] :or {title "" extra ""}}]
  #jsx [:label {:class (str "block mt-1 mb-2 text-sm font-medium text-gray-900 dark:text-white " extra)}
        title])
