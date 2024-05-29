(ns pages.home
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../normad.mjs" :as n]
            ["@solidjs/router" :refer [useParams]]
            ["../components/activity.jsx" :as a]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn HomePage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        params (useParams)
        a (println "home")
        data (createMemo #(n/pull store [:pages/id :home] [:activity]))
        ]
    #jsx [:div {}
          [a/ui-activity]]))
