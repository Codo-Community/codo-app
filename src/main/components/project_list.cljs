(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../comp.mjs" :as comp]
            ["./project_item.jsx" :as pi]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc ProjectList [this {:keys [component/id projects]}]
  #jsx [:div {:class "flex flex-col mt-4 mb-4 p-2 items-center"}
        [:div {:class "flex flex-col gap-3"}
         [For {:each (projects)}
          (fn [entity i]
            #jsx [pi/ui-project-item entity])]]])

(def ui-project-list (comp/comp-factory ProjectList AppContext))
