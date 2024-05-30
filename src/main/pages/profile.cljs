(ns components.profile
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../components/userprofile.jsx" :as up]
            ["../normad.mjs" :as n]
            ["@solidjs/router" :refer [useParams]]
            ["../comp.mjs" :as comp]
            ["../transact.mjs" :as t]
            ["../utils.mjs" :as utils]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn ProfilePage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        params (useParams)
        a (println "params: " params)
        data (createMemo #(n/pull store [:pages/id :profile] [:user]))
        ]
    #_(onMount ((on-click-mutation ctx (data)) nil))
    #jsx [:div {}
          #_[u/ui-user (data)]
          [up/ui-user-profile (data)]]))

#_(defc ProfilePage [this {:keys [user]}]
  #jsx [:div {}
          #_(ui-user (data))
          (UserProfile (user))])
