(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../comp.cljs" :as comp]
            ["./project_item.cljs" :as pi]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc ProjectList [this {:keys [component/id projects]}]
  #jsx [:div {:class "flex flex-col px-4 items-center"}
        [:div {:class "flex flex-col gap-4"}
         [Index {:each (projects)}
          (fn [entity i]
            #jsx [pi/ui-project-item {:& {:ident entity}}])]]])

(def ui-project-list (comp/comp-factory ProjectList AppContext))
