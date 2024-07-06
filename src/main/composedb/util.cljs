(ns main.composedb.util
  (:require ["../utils.cljs" :as utils]
            ["./client.cljs" :as cli]
            ["../geql.cljs" :as geql]
            [squint.string :as str]
            ["../components/alert.cljs" :as alert]
            ["../normad.cljs" :as n]
            ["did-session" :refer [DIDSession]]
            ["../transact.cljs" :as t]))

(defn handle-fn [ctx f {:keys [check-session?] :or {check-session? true}}]
  (fn [response]
    (println "rp: r " response)
    (let [res (-> response :data)
          res2 (utils/add-ns res)]
      (println "res2: " res2)
      (if (first f)
        ((first f) res2)
        (t/add! ctx res2 {:check-session? check-session?})))))

(defn execute-gql-query [ctx query vars & f]
  (.then (cli/exec-query query vars) (handle-fn ctx f {:check-session? false})))

(defn execute-gql-mutation [ctx mutation vars & f]
  (.then (cli/exec-mutation mutation vars) (handle-fn ctx f {:check-session? true})))

(defn ^:async has-session-for [account-id resources]
  (println "resources: " (js/typeof resources))
  (.hasSessionFor DIDSession account-id {:resources resources}))

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
  (println "converted gql: " (remap-query query))
  (apply (partial execute-gql-query ctx (remap-query query) {}) f))
