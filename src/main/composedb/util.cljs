(ns main.composedb.util
  (:require ["../utils.cljs" :as utils]
            ["./client.cljs" :as cli]
            ["../geql.cljs" :as geql]
            [squint.string :as str]
            ["../transact.cljs" :as t]))

#_(defn execute-gql-query [ctx query vars & f]
  (-> (.executeQuery (:compose @cli/client) query vars)
      (.then (fn [response]
               (println "resp: " response)
               ))))

(defn execute-gql-mutation [ctx query vars & f]
  (-> (.executeQuery (:compose @cli/client) query vars)
      (.then (fn [response]
               (println "resp: " response)
               (let [res (-> response :data)]
                 (if (first f)
                   ((first f) res)
                   (t/add! ctx (utils/nsd res ns))))))))

(defn remap-query [query]
  (let [k (if (first (keys query)) (str/split (first (keys query)) ","))
        ;a (println "gql: " k)
        query (if (utils/ident? k)
                {(first (keys query)) (utils/remove-ns (first (vals query)))}
                query)
        ;a (println "gql: " query)
        query (geql/eql->graphql query)]
    #_(println "gql: " (first (keys query)))
    #_(println "gql:f " (utils/remove-ns (first (vals query))))

    query))

(defn execute-eql-query [ctx query & f]
  (apply (partial execute-gql-mutation ctx (remap-query query) {}) f))
