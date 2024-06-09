(ns main.composedb.util
  (:require ["../utils.mjs" :as utils]
            ["../transact.mjs" :as t]))


(defn execute-query [ns ctx query]
(-> (.executeQuery (:compose @cli/client) query)
        (.then (fn [response]
                 (let [res (-> response :data)]
                   (t/add! ctx (utils/nsd res :user)
                           {:replace [:pages/id :profile :user]}))))))
