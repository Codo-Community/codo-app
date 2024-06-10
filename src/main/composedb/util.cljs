(ns main.composedb.util
  (:require ["../utils.mjs" :as utils]
            ["./client.mjs" :as cli]
            ["../geql.mjs" :as geql]
            [squint.string :as str]
            ["../transact.mjs" :as t]))


(defn execute-eql-query [ctx query ns & f]
  (let [k (if (first (keys query)) (str/split (first (keys query)) ","))
        query (if (utils/ident? k)
                {(first (keys query)) (utils/remove-ns (first (vals query)))}
                query)
        query (geql/eql->graphql query)]
    (-> (.executeQuery (:compose @cli/client) query)
        (.then (fn [response]
                 (let [res (-> response :data)]
                   (if-not (nil? (first f))
                     ((first f) res)
                     (t/add! ctx (utils/nsd res ns)))))))))

(defn execute-gql-mutation [ctx query vars & f]
  (-> (.executeQuery (:compose @cli/client) query vars)
      (.then (fn [response]
               (let [res (-> response :data)]
                 (if (first f)
                   ((first f) res)
                   (t/add! ctx (utils/nsd res ns))))))))
