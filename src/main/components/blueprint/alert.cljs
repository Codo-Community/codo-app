(ns main.components.blueprint.alert)

(defn alert [{:keys [title message type]}]
  #jsx [:div {:id "alert-1"
              :class (str "flex items-center p-4 mb-4 rounded-lg border-1 z-50 dark:bg-opacity-70 "
                          (condp = type
                            :warning "dark:bg-yellow-900 dark:text-yellow-400"
                            :error "dark:bg-red-900 dark:bg-opacity-20 dark:text-red-400 dark:border-red-600"
                            :info "dark:bg-blue-900 dark:bg-opacity-20 dark:text-blue-400"
                            "bg-gray-600 dark:bg-black"))
              :role "alert"}
        [:div {:class (condp = type
                        :warning "i-tabler-alert-circle text-yellow-800"
                        :error "i-tabler-alert-circle text-red-800"
                        :info "i-tabler-info-circle text-blue-800"
                        :success "i-tabler-check-circle text-green-800"
                        "i-tabler-info-circle")}]
        [:span {:class "sr-only"} title]
        [:div {:class "ms-3 text-sm font-medium text-gray-400 pr-2"}
         message]
        [:button {:type "button"
                  :class (str "ms-auto -mx-1.5 -my-1.5 rounded-lg focus:ring-2 focus:ring-blue-400 p-1.5
                          inline-flex items-center justify-center h-8 w-8 "
                              (condp = type
                                :warning "text-yellow-800 dark:text-yellow-400"
                                :error "text-red-800 dark:text-red-400"
                                :info "text-blue-800 dark:text-blue-400"
                                :success "text-green-800 dark:text-green-400"
                        "dark:text-white"))
                  :data-dismiss-target "#alert-1"
                  :aria-label "Close"}
         [:span {:class "sr-only"} "Close"]
         [:div {:class "i-tabler-x"}]]])
