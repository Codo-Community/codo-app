(ns main.components.category.menu
  (:require ["../blueprint/dropdown.cljs" :as d]
            ["solid-js" :refer [onMount]]
            ["../blueprint/tooltip.cljs" :as to]
            ["../../comp.cljs" :as comp]
            ["flowbite" :refer [initDropdowns initTooltips]]))

(defn ui-category-menu [{:keys [this parent id] :category/keys [id name color children] :as props}]
  (onMount #(do (initDropdowns) (initTooltips)))
  #jsx [:div {:class "flex flex-col relative w-fit"}
        [to/tooltip {:id "tooltip-color" :content "Color"}]
        [:span {:class "flex gap-2 p1 items-center"}
         [:button {:class "i-tabler-plus dark:text-white dark:text-opacity-70 hover:text-opacity-100"
                   :onClick #(comp/mutate! this {:add :new
                                                 :append [:category/id id :category/children]})}]
         [:button {:class "i-tabler-palette"
                   :data-tooltip-target "tooltip-color"
                   :data-dropdown-toggle "color-dropdown"
                   :data-tooltip-placement "top"
                   :data-dropdown-trigger "hover"}]
         [:button {:class "i-tabler-trash text-red-500"
                   :onClick #(comp/mutate! this {:remove [:category/id id]
                                                 :from [:category/id (:parent props) :category/children]
                                                 :cdb true})}]]
        [:div {:class "z-10 w-fit"}
         [d/dropdown {:& {:id "color-dropdown"
                                 :items (fn [] [{:value "red" :id "red"}
                                                {:value "gray" :id "gray"}
                                                {:value "yellow" :id "yellow"}
                                                {:value  "green" :id "green"}
                                                {:value "blue" :id "blue"}])
                                 :selected (fn [] color)
                                 :extra-class "hidden"
                                 :on-change #(do (println (:ident props)) (comp/set! this ((:ident props)) :category/color %))}}]]])

#_(def ui-category-menu (comp/comp-factory CategoryMenu AppContext))
