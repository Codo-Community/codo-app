(ns  main.components.project.proposal
  (:require ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defc Comment [this {:comment/keys [id text author]
                     :or {id (sqeave/uuid) content "Default comment" author {:name "Anonymous"}}}]
  #jsx [:div {:class "border border-gray-300 rounded-md p-2 mb-2"}
        [:p {:class "font-bold"} (:name author)]
        [:p {:class "text-sm"} content]
        [:div {:class "flex items-center gap-2"}
         [:span {:class "text-green-500"} upvotes]
         [:button {:class "text-green-500 hover:text-green-700"
                   :onClick #(println "Upvote comment" id)} "Upvote"]
         [:span {:class "text-red-500"} downvotes]
         [:button {:class "text-red-500 hover:text-red-700"
                   :onClick #(println "Downvote comment" id)} "Downvote"]]])
