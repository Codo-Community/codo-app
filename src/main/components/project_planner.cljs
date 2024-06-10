(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../comp.mjs" :as comp]
            ["../composedb/util.mjs" :as cu]
            ["./project_item.jsx" :as pi]
            ["../Context.mjs" :refer [AppContext]]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

(defc ProjectPlanner [this {:project/keys [name {category [:id]}]}]
  #jsx [:div {:class "flex flex-col mt-4 mb-4 p-2 items-center"}
        (str category)])

(def ui-project-planner (comp/comp-factory ProjectPlanner AppContext))

(defn load-project-planner [ctx ident]
  (cu/execute-query ctx {ident (aget (ProjectPlanner.) "query")} :project (fn [r] (println "load project: " r))))
