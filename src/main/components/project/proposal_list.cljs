(ns main.components.project.proposal-list
  (:require ["solid-js" :refer [Show For]]
            ["./proposal.cljs" :as p]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defc ProposalList [this {:keys [project/id proposals] :as data :or {}}]
  #jsx [:div {:class "dark:(placeholder-gray-400 focus:ring-blue-500 border-gray-700) w-full px-4
                          text-md overflow-auto"}
          [:div {:class "position-relative overflow-y-auto overflow-x-hidden"}
           [Show {:when (not (empty? (proposals)))}
            [For {:each (proposals)}
             (fn [p _]
               #jsx [p/Proposal {:& {:ident p}}])]]]])
