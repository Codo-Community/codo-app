(ns main.components.category.menu
  (:require ["../blueprint/dropdown.cljs" :as d]
            ["solid-js" :refer [onMount Show]]
            ["../blueprint/tooltip.cljs" :as to]
            ["../../comp.cljs" :as comp]
            ["../../transact.cljs" :as t]
            ["flowbite" :refer [initDropdowns initTooltips]]
            ["../../utils.cljs" :as u]
            ["./category.cljs" :as c]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc CategoryMenu [this {:category/keys [id {creator [:ceramic-account/id]}]
                          :or {id (u/uuid) name "Category" children nil color :gray
                               proposals []} :as data}]
  (do
    (onMount #(do (initDropdowns) (initTooltips)))
    #jsx [:div {:class "flex flex-col relative w-fit h-full items-center"}
          [to/tooltip {:id "tooltip-color" :content "Color"}]
          [:span {:class "flex gap-2 items-center"}
           [Show {:when true #_(comp/viewer? this (creator))}
            [:button {:class "i-tabler-plus dark:text-white dark:text-opacity-70 hover:text-opacity-100"
                      :onClick #(let [link-id (u/uuid)
                                      child-id (u/uuid)]
                                  (comp/mutate! this {:add #:category{:id child-id :name "Category"
                                                                      :color :gray
                                                                      :children []
                                                                      :link link-id
                                                                      :creator (comp/viewer-ident this)
                                                                      :created (.toLocaleDateString (js/Date.) "sv")}})
                                  (comp/mutate! this {:add #:category-link{:id link-id
                                                                           :parentID (id)
                                                                           :parent [:category/id (id)]
                                                                           :child [:category/id child-id]
                                                                           :childID child-id}
                                                      :append [:category/id (id) :category/children]}))}]]
           [:button {:class "i-tabler-plus dark:text-white dark:text-opacity-70 hover:text-opacity-100"
                     :onClick #(t/add! ctx {:proposal/id (u/uuid)
                                            :proposal/name "New proposal"
                                            :proposal/author (comp/viewer-ident this)
                                            :proposal/created (.toLocaleDateString (js/Date.) "sv")
                                            :proposal/status :EVALUATION
                                            :proposal/parentID (id)}
                                       {:append [:category/id (id) :category/proposals]})}]
           #_[:button {:class "i-tabler-palette"
                       :data-tooltip-target "tooltip-color"
                       :data-dropdown-toggle "color-dropdown"
                       :data-tooltip-placement "top"
                       :data-dropdown-trigger "hover"}]
           [Show {:when (comp/viewer? this (creator))}
            [:button {:class "i-tabler-trash text-red-500"
                      :onClick #(do
                                  (comp/mutate! this {:remove [:category-link/id (:link props)]
                                                      :from [:category/id (:parent props) :category/children]
                                                      :cdb true})
                                  (when (not (u/uuid? (id)))
                                    (c/remove-category-remote ctx (:link props))))}]]]
          [:div {:class "z-10 w-fit"}
           [d/dropdown {:& {:id "color-dropdown"
                            :items (fn [] [{:value "red" :id "red"}
                                           {:value "gray" :id "gray"}
                                           {:value "yellow" :id "yellow"}
                                           {:value  "green" :id "green"}
                                           {:value "blue" :id "blue"}])
                            :selected (fn [] (color))
                            :extra-class "hidden"
                            :on-change #(do (println (:ident props)) (comp/set! this ((:ident props)) :category/color %))}}]]]))

(def ui-category-menu (comp/comp-factory CategoryMenu AppContext))
