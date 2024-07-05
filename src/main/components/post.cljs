(ns  main.components.project.proposal
  (:require ["solid-js" :refer [createSignal Show For createMemo useContext]]
            ["../utils.cljs" :as utils]
            ["../comp.cljs" :as comp]
            ["./comment.cljs" :as comment]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Post [this {:post/keys [id body comments {author [:id]}]
                  :or {id (utils/uuid) body "Default content" comments []}}]
  #jsx [:div {:class "border border-indigo-700 rounded-md p-4 mb-4"}
        [:h2 {:class "font-bold text-lg mb-2"} (title)]
        [:p {:class "mb-4"} (body)]
        [:div {:class "mt-4"}
         [:h3 {:class "font-bold text-md mb-2"} "Comments"]
         #_[For {:each (comments)}
            (fn [comment _]
              #jsx [comment/ui-comment comment])]]])

(def ui-post (comp/comp-factory Post AppContext))
