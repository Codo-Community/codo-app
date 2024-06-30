(ns main.components.blueprint.tabs
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]))

(defn Tabs [{:keys [id data-tabs-toggle items]}]
  #jsx [:div {:class "border-b border-gray-200 dark:border-gray-700"}
        [:ul {:class "flex flex-wrap -mb-px text-sm font-medium text-center text-gray-500 dark:text-gray-300"
              :id "default-tab" :data-tabs-toggle (str "#" data-tabs-toggle) :role "tablist"}
         [:li {:class "me-2" :role "presentation"}
          [For {:each (items)}
           (fn [{:keys [title]} _]
             #jsx [:button {:class "inline-block p-2 border-b-1 rounded-t-lg hover:text-gray-600 hover:border-gray-300 dark:hover:text-gray-300"
                       :id "dashboard-tab"
                       :data-tabs-target (str "#" "dashboard")
                       :type "button" :role "tab" :aria-controls "dashboard" :aria-selected "false"}
              [:span {:class "flex gap-2 items-center"} [:div {:class "i-tabler-list-tree w-4 h-4"}] title]])]]]])

(defn TabContent [{:keys [id item]}]
  #jsx [:div {:id id
              :class "hidden p-2 rounded-lg bg-gray-50 dark:bg-gray-800" :role "tabpanel" :aria-labelledby (str id "-tab")}
        ])
