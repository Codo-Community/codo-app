(ns main.components.category.view
  (:require ["solid-js" :refer [For createSignal onMount createContext]]
            ["flowbite" :refer [initFlowbite]]
            ["./category.cljs" :as c]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./context.cljs" :refer [FilterContext]]
            ["../blueprint/input.cljs" :as in]
            ["../blueprint/button.cljs" :as b])
  (:require-macros [sqeave :refer [defc]]))

(defc CategoryView [this {:category/keys [id created] :as data :or {}
                          :local {show-proposals? true from nil}}]
  (let [
        [show-proposals? setshow-proposals?] (createSignal true)
        [from setfrom] (createSignal)
        FilterContext (createContext )
        ]
    (onMount #(initFlowbite))
    #jsx [:div {:class "relative flex flex-col gap-2"}

          [:div {:class "absolute top-0 right-0 w-fit"}
           [:div {:data-dropdown-toggle "filter-dropdown"}
            [b/button {:icon "i-tabler-filter"
                       :extra-class "!p-1 !h-6"}]]]

          ;; dd
          [:div {:id "filter-dropdown"
                 :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow w-fit dark:(bg-black divide-gray-600 border-gray-600) border-1 p-2"}
           [:h2 {:class "font-semibold text-gray-800 dark:text-gray-200"}
            "Filters"]
           [:ul {:class "text-sm text-gray-700 dark:text-gray-200"}
            [:li {}
             [in/boolean-input {:label "Show proposals:"
                                :inline true
                                :value show-proposals? #_(:show-proposals? (local))
                                :on-change #(setshow-proposals? (not (show-proposals?))) #_(setLocal (assoc (local) :show-proposals? (not (:show-proposals? (local)))))}]]
            #_[:li {}
             [in/input {:label "From"
                        :value #(:from (local))
                        :datepicker ""
                        :type "date"
                        :on-change (fn [e] #(setLocal (assoc (local) :from (-> e :target :value))))}]]]]

          [FilterContext.Provider {:value {:show-proposals? show-proposals? :from from}}
           [c/Category {:& (merge props {:show-proposals? show-proposals? #_(:show-proposals? (local))
                                         :from from #_(:from (local))})}]]]))
