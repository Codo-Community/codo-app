(ns main.components.category.menu
  (:require ["../blueprint/dropdown.cljs" :as d]
            ["../../Context.cljs" :refer [AppContext]]
            ["../../comp.cljs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defn ui-category-menu [{:keys [this data parent id] :as props}]
  #jsx [:div {:class "flex flex-col relative"}
        [:span {:class "flex gap-2 items-center"}
         [:button {:class "i-tabler-plus"
                   :onClick #(comp/mutate! this {:add :new
                                                 :append [:category/id (:category/id (data)) :category/children]})}]
         [:button {:class "i-tabler-palette "
                   :data-dropdown-toggle "color-dropdown"
                   ;:data-dropdown-trigger "hover"
                   }]
         [:button {:class "i-tabler-trash text-red-500"
                   :onClick #(comp/mutate! this {:remove [:category/id (id)]
                                                 :from [:category/id (:parent props) :category/children]
                                                 :cdb true})}]]
        [:div {:class "z-10"}
         [d/dropdown-select {:id "color-dropdown"
                             :items (fn [] [{:value "red" :id "red"}
                                            {:value "gray" :id "gray"}
                                            {:value "yellow" :id "yellow"}
                                            {:value  "green" :id "green"}
                                            {:value "blue" :id "blue"}])
                             :selected #(:category/color (data))
                             :extra-class "hidden"
                             :on-change #(comp/set! this (:ident props) :category/color %)}]]])

#_(def ui-category-menu (comp/comp-factory CategoryMenu AppContext))
