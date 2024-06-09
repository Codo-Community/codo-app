(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../comp.mjs" :as comp]
            ["./project_item.jsx" :as pi]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc ProjectReport [this {:project/keys [id name description chain {contract [:contract/chain]}]}]
  #jsx [:div {:class "flex flex-col mt-4 mb-4 p-2 items-center"}
        [:div {:class "flex flex-col gap-3"}
         ]])

(def ui-project-report (comp/comp-factory ProjectReport AppContext))
