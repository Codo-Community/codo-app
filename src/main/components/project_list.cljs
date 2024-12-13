(ns pages.search
  (:require ["solid-js" :refer [Index]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./project_item.cljs" :as pi])
  (:require-macros [sqeave :refer [defc]]))

(defc ProjectList [this {:keys []}]
  (let [projects (:projects props)]
    #jsx [:div {:class "flex flex-col px-4 items-center"}
          [:div {:class "flex flex-col gap-4"}
           [For {:each (projects)}
            (fn [entity i]
              #jsx [:div {} (println "en:" entity) [pi/ProjectItem {:& {:ident entity}}]])]]]))
