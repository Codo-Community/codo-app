(ns main.components.blueprint.textarea)

(defn textarea [{:keys [title on-change value placeholder]}]
  #jsx  [:div {:class "rounded-md"}
         [:label {:class "block mb-3 text-sm font-medium text-gray-900 dark:text-white"}
          title]
         [:textarea {:rows 4
                     :placeholder placeholder
                     :onChange on-change
                     :value (value)
                     :class "block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-md
                             border border-gray-300 focus:ring-blue-500 focus:border-blue-500
                             dark:(bg-black border-gray-600 placeholder-gray-400 text-white focus:ring-blue-500 focus:border-blue-500)"}]])
