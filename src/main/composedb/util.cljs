(ns main.composedb.util
  (:require ["../utils.cljs" :as utils]
            ["./client.cljs" :as cli]
            ["../geql.cljs" :as geql]
            [squint.string :as str]
            ["lodash" :as l :refer [capitalize trim]]
            ["../components/alert.cljs" :as alert]
            ["../normad.cljs" :as n]
            ["did-session" :refer [DIDSession]]
            ["../transact.cljs" :as t]))

(defn handle-fn [ctx f {:keys [check-session?] :or {check-session? true}}]
  (fn [response]
    (println "rp: r " response)
    (let [res (-> response :data)
          res2 (utils/add-ns res)]
      (println "res3: " res)
      (println "res3: " res2)
      (if (first f)
        ((first f) res2)
        (t/add! ctx res2 {:check-session? check-session?})))))

(defn handle-fn-mutation [ctx f {:keys [check-session? tempid] :or {check-session? true}}]
  (fn [response]
    (let [mutation-name (first (keys (-> response :data)))
          res (get-in response [:data mutation-name :document])
          res2 (utils/add-ns res)]
      (println "res2:1: " res)
      (println "res2: " res2)
      (println "res2: "(js/typeof res2))
      (println "res2: "(map? res2))
      (println "res2: " (contains? res :__typename))
      (println "f: " (first f))
      (if (first f)
        ((first f) res2))
      (when tempid
        (println "swap: " tempid)
        (t/swap-uuids! ctx [(str (l/lowerCase (:__typename res)) "/id") tempid] (:id res)))
      #_(t/add! ctx res2 {:check-session? check-session?}))))

(defn generic-mutation [mutation-name type]
  (let [n (utils/pascal-case (str type " "  mutation-name))
        n (str/replace n #" " "")
        i (str n "Input")]
    (str "mutation " n "($i: " i "!) {"
         (str/replace (utils/camel-case (str type " "  mutation-name)) #" " "") "(input: $i) {"
         "document { id __typename } } }")))

(defn execute-gql-query [ctx query vars & f]
  (-> (.then (cli/exec-query query vars) (handle-fn ctx f {:check-session? false}))
      (.catch (fn [err]
                (println "error: " err)
                (println "query: " query)
                (println "vars: " query)
                (t/alert-error ctx err)))))

(defn execute-gql-mutation-simple [ctx mutation-name vars {:keys [f check-session] :as opts}]
  (let [vars (utils/remove-ns vars)
        vars (utils/drop-false vars)
        id (:id vars)
        vars (if (utils/uuid? id)
               (dissoc vars :id)
               vars)
        vars {:i {:content vars}}
        mutation (if (string? mutation-name)
                   (generic-mutation mutation-name (if (utils/uuid? id) "create" "update"))
                   mutation-name)]
    (println "mutation: " mutation)
    (-> (.then (cli/exec-mutation mutation vars)
               (handle-fn-mutation ctx f (if (utils/uuid? id)
                                           (assoc opts :tempid id) opts)))
        (.catch (fn [err]
                (println "error: " err)
                (t/alert-error ctx err))))))

(defn execute-gql-mutation [ctx mutation vars & f]
  (-> (.then (cli/exec-mutation mutation vars) (handle-fn-mutation ctx f {:check-session? true}))
      (.catch (fn [err]
                (println "error: " err)
                (println "mutation: " mutation)
                (println "vars: " mutation)
                (t/alert-error ctx err)))))

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
