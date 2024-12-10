(ns pages.search
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["../composedb/util.cljs" :as cu])
  (:require-macros [sqeave :refer [defc]]))

(defc ProjectPlanner [this {:project/keys [name {category [:id]}]}]
  #jsx [:div {:class "flex flex-col mt-4 mb-4 p-2 items-center"}
        (str category)])

(defn load-project-planner [ctx ident]
  (cu/execute-query ctx {ident ProjectPlanner.query} :project (fn [r] (println "load project: " r))))
