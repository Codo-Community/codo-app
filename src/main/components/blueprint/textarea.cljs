(ns main.components.blueprint.textarea
  (:require ["./label.cljs" :as l]))

(defn textarea [{:keys [title on-change value placeholder]}]
  #jsx  [:div {:class "rounded-md"}
         [l/label {:title title}]
         [:textarea {:rows 4
                     :placeholder placeholder
                     :onChange on-change
                     :value (or (value) "")
                     :class "block p-2.5 w-full text-sm text-gray-900 bg-gray-50 rounded-md
                             border border-gray-300 focus:ring-blue-500 focus:border-blue-500
                             dark:(bg-black border-gray-600 placeholder-gray-400 text-white focus:ring-blue-500 focus:border-blue-500)"}]])
