(ns main.components.category.menu
  (:require ["../blueprint/dropdown.cljs" :as d]
            ["../../Context.cljs" :refer [AppContext]]
            ["../../comp.cljs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defc CategoryMenu [this {:category/keys [id color] :or {id (comp/uuid) color "gray"}}]
  #jsx [:span {:class "flex gap-2 items-center"}
        [:button {:class "i-tabler-plus"
                  :onClick #(comp/mutate! this {:add :new
                                                :append [:category/id (id) :category/children]})}]
        [:button {:class "i-tabler-palette "
                  :data-dropdown-toggle "color-dropdown"
                  :data-dropdown-trigger "hover"}]
       #_ [d/dropdown-select {:id "color-dropdown"
                            :items (fn [] [{:value "red" :id "red"} {:value  "green" :id "red"} {:value "blue" :id "blue"}])
                            :selected color
                            :onSelect #(comp/set! this :category/color %)}]
        #_[:button {:class "i-tabler-icons"}]
        #_[:button {:class "i-tabler-edit"}]
        [:button {:class "i-tabler-trash text-red-500"
                  :onClick #(comp/mutate! this {:remove [:category/id (id)]
                                                :from [:category/id (:parent props) :category/children]
                                                :cdb true})}]])

(def ui-category-menu (comp/comp-factory CategoryMenu AppContext))
