(ns components.profile
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../components/userprofile.cljs" :as up]
            ["../normad.cljs" :as n]
            ["@solidjs/router" :refer [useParams]]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../utils.cljs" :as utils]
            ["../Context.cljs" :refer [AppContext]])
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
