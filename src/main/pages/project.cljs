(ns components.project
  (:require ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["../utils.cljs" :as utils]
            ["../transact.cljs" :as t]
            ["./blueprint/button.cljs" :as b]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn ProjectPage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(n/pull store [:pages/id :profile] [:user]))]
    #_(onMount ((on-click-mutation ctx (data)) nil))
    #jsx [:div {}
          #_[u/ui-user (data)]
          [up/ui-project (data)]]))

#_(defc ProfilePage [this {:keys [user]}]
  #jsx [:div {}
          #_(ui-user (data))
          (UserProfile (user))])
