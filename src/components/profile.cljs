(ns components.profile
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["./userprofile.jsx" :as up]
            ["../composedb/client.mjs" :as cli]
            ["../normad.mjs" :as n]
            ["./blueprint/input.jsx" :as in]
            ["./blueprint/button.jsx" :as b]
            ["./user.jsx" :as u :refer [ui-user]]
            ["../comp.mjs" :as comp]
            ["../transact.mjs" :as t]
            ["../utils.mjs" :as utils]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn ProfilePage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(n/pull store [:pages/id :profile] [:user]))]
    #_(onMount ((on-click-mutation ctx (data)) nil))
    #jsx [:div {}
          #_[u/ui-user (data)]
          [up/ui-user-profile (data)]]))

#_(defc ProfilePage [this {:keys [user]}]
  #jsx [:div {}
          #_(ui-user (data))
          (UserProfile (user))])
