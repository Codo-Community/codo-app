(ns main.components.category.view
  (:require ["solid-js" :refer [For createSignal onMount]]
            ["flowbite" :refer [initFlowbite]]
            ["./category.cljs" :as c]
            ["../../comp.cljs" :as comp]
            ["../../Context.cljs" :refer [AppContext]]
            ["../blueprint/input.cljs" :as in]
            ["../blueprint/button.cljs" :as b])
  (:require-macros [comp :refer [defc]]))

(defc CategoryView [this {:category/keys [id created] :as data :or {}
                          :local {show-proposals? true from nil}}]
  (do
    (onMount #(initFlowbite))
    #jsx [:div {:class "flex flex-col gap-2"}
          [:div {:class "flex gap-2 w-full"}
           [b/button {:icon "i-tabler-filter"
                      :extra-class "!p-1 !h-6"
                      :data-dropdown-toggle "filter-dropdown"}]
           [:div {:id "filter-dropdown"
                  :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow w-fit dark:(bg-black divide-gray-600 border-gray-600) border-1"}
            [:ul {:class "py-2 text-sm text-gray-700 dark:text-gray-200"}
             [:li {} [in/boolean-input {:label "Show Proposals?"
                                        :value #(:show-proposals? (local))
                                        :on-change #(setLocal (assoc (local) :show-proposals? (not (:show-proposals? (local)))))}]]
             [:li {} [in/input {:label "From"
                                :value #(:from (local))
                                :datepicker ""
                                :type "date"
                                :on-change (fn [e] #(setLocal (assoc (local) :from (-> e :target :value))))}]]]]]
          [c/ui-category {:& props}]]))

(def ui-category-view (comp/comp-factory CategoryView AppContext))
