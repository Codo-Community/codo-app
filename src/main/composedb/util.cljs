(ns main.composedb.util
  (:require ))


(defn execute-query [ns ctx]
(-> (.executeQuery (:compose @cli/client) query-from-acc)
        (.then (fn [response]
                 (js/console.log "response: " response)
                 (let [res (-> response :data)]
                   (t/add! ctx (utils/nsd res :user)
                           {:replace [:pages/id :profile :user]}))))))
