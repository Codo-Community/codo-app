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

(defc CategoryMenu [this {:category/keys [id {creator [:id :isViewer]}]
                          :or {id (u/uuid) name "Category" children nil
                               proposals []} :as data}]
  (do
    (onMount #(do (initDropdowns) (initTooltips)))
    #jsx [:div {:class "flex flex-col relative w-fit"} 
          [:span {:class "flex gap-2 items-center"}
           [Show {:when (:isViewer (creator))}
            [:button {:class "bg-zinc-700 text-white font-bold py-0.5 px-1.5 rounded-md"
                      :onClick #(do
                                  #_(println "new-data: " {:id (u/uuid) :name "Category" :color :gray
                                                           :parentID (id)})
                                  (comp/mutate! this {:add :new #_#:category{:id (u/uuid) :name "Category" :color :gray
                                                                             :parentID (id)} #_(this.new-data {:parentID (id)})
                                                      :append [:category/id (id) :category/children]}))} "add category"]]
           [:button {:class "bg-[#121313] dark:bg-zinc-700 text-white font-bold py-0.5 px-1.5 rounded-md"
                     :onClick #(t/add! ctx {:proposal/id (u/uuid)
                                            :proposal/name "New Proposal"
                                            :proposal/created (.toLocaleDateString (js/Date.) "sv")
                                            :proposal/status :EVALUATION
                                            :proposal/parentID (id)}
                                       {:append [:category/id (id) :category/proposals]
                                        :check-session? true})} "add proposal"]]
          [:div {:class "z-10 w-fit"}]]))

(def ui-category-menu (comp/comp-factory CategoryMenu AppContext))
