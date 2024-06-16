(ns main.composedb.events
  (:require ["solid-js" :refer [useContext]]
            ["../transact.cljs" :as t]
            ["../utils.cljs" :as u]))

(def id-to-ns {:kjzl6hvfrbw6c9ddpqfs4s455c7r6zctrfapmk9bv1ilshn0vdv94mvlfyxhycy :category
               :kjzl6hvfrbw6cab7zruo9yfeyoagv9kugqao769lszwy5lbgwinkrp87kb5rtfw :user})

(defn handle [ctx data instance-id model-id]
  (t/add! ctx (u/nsd (assoc data :id instance-id) (get id-to-ns model-id))))
