(ns main.composedb.util
  (:require ["../utils.cljs" :as utils]
            ["./client.cljs" :as cli]
            ["../geql.cljs" :as geql]
            [squint.string :as str]
            ["../components/alert.cljs" :as alert]
            ["../normad.cljs" :as n]
            ["did-session" :refer [DIDSession]]
            ["../transact.cljs" :as t]))

(defn handle-fn [ctx response & f]
  (fn [response]
    (println "rp: r " response)
    (let [res (-> response :data)]
      (if (first f)
        ((first f) res)
        (t/add! ctx (utils/nsd res ns))))))

(defn execute-gql-query [ctx query vars & f]
  (.catch
   (.then (cli/exec-query query vars) (fn [response]
                                        (println "resp: " response)
                                        (let [res (-> response :data)]
                                          (if (first f)
                                            ((first f) res)
                                            (t/add! ctx (utils/nsd res ns))))))
   #_(alert/alert-error ctx)
   (fn [error]
       (println "error query " query)
       (println "error" error)
       (println "vars" vars)

       (t/add! ctx {:component/id :alert
                    :title "Error"
                    :type :error
                    :visible? true
                    :interval 4000
                    :message (str error)}))))

(defn execute-gql-mutation [ctx mutation vars & f]
  (let [session (-> (:compose @cli/client) :did :_id) #_(n/pull (:store ctx) [:viewer/id 0] [:viewer/session])]
    (if-not session
      ((alert/alert-error ctx) (js/Error. "Sign in to to make changes."))
      (.catch (.then (cli/exec-mutation mutation vars) (fn [response]
                                                         (let [res (-> response :data)]
                                                           (if (first f)
                                                             ((first f) res)
                                                             (t/add! ctx (utils/nsd res ns))))))
              (alert/alert-error ctx)))))

(defn ^:async has-session-for [account-id resources]
  (println "r:" resources)
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
  (apply (partial execute-gql-query ctx (remap-query query) {}) f))
