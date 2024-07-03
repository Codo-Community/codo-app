(ns main.components.blueprint.alert)

(defn alert [{:keys [title message type]}]
  #jsx [:div {:id "alert-1"
              :class (str "flex items-center p-4 mb-4 text-blue-800 rounded-lg "
                          (condp = type
                        :warning "dark:bg-yellow-800 dark:text-yellow-400"
                        :error "dark:bg-re-800 dark:text-re-400"
                        :info "dark:bg-blue-800 dark:text-blue-400"
                        "bg-gray-800 dark:bg-black"))
              :role "alert"}
        [:div {:class (condp = type
                        :warning "i-tabler-alert-circle text-yellow-800"
                        :error "i-tabler-alert-circle text-red-800"
                        :info "i-tabler-info-circle text-blue-800"
                        :success "i-tabler-check-circle text-green-800"
                        "i-tabler-info-circle")}]
        [:span {:class "sr-only"} title]
        [:div {:class "ms-3 text-sm font-medium text-gray-400"}
         message]
        [:button {:type "button"
                  :class "ms-auto -mx-1.5 -my-1.5 bg-blue-50 text-blue-500 rounded-lg focus:ring-2 focus:ring-blue-400 p-1.5 hover:bg-blue-200 inline-flex items-center justify-center h-8 w-8 dark:bg-gray-800 dark:text-blue-400 dark:hover:bg-gray-700"
                  :data-dismiss-target "#alert-1"
                  :aria-label "Close"}
         [:span {:class "sr-only"} "Close"]
         [:div {:class "i-tabler-x"}]]])
