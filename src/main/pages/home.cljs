(ns pages.home
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../normad.cljs" :as n]
            ["@solidjs/router" :refer [useParams]]
            ["../components/activity.cljs" :as a]
            ["../utils.cljs" :as u]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn HomePage [props]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        params (useParams)
        data (createMemo #(n/pull store [:pages/id :home] [:activity]))
        ]
    #jsx [:div {}
          #_[:div {} (u/get-item :bookmarked-projects)]
          [a/ui-activity {:& {:ident (fn [])}}]]))
