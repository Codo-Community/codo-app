(ns components.profile
  (:require ["../components/userprofile.cljs" :as up]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defc ProfilePage [this {:keys [pages/id user] :or {pages/id :profile :user {:user/id 0
                                                                             :user/ethereum-address "0x0"}}}]
  (let
   #_(onMount ((on-click-mutation ctx (data)) nil))
   #jsx [up/UserProfile {:ident (user)}]))
