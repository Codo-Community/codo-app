(ns main.composedb.events
  (:require ["../transact.cljs" :as t]
            ["../utils.cljs" :as u]
            ["../../__generated__/definition.js" :refer [definition]]))

(defn create-id-map [models]
  (reduce (fn [acc [k v]] (assoc acc (get v :id) (u/kebab-case k))) {} models))

(def id-map (create-id-map (:models definition)))

(defn handle [ctx data instance-id model-id]
  (t/add! ctx (u/nsd (assoc data :id instance-id) (get id-map model-id))))
