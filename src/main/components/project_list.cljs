(ns pages.search
  (:require ["solid-js" :refer [Index]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./project_item.cljs" :as pi])
  (:require-macros [sqeave :refer [defc]]))

(defc ProjectList [this {:keys [component/id projects]}]
  #jsx [:div {:class "flex flex-col px-4 items-center"}
        [:div {:class "flex flex-col gap-4"}
         [Index {:each (projects)}
          (fn [entity i]
            #jsx [pi/ProjectItem {:& {:ident entity}}])]]])
