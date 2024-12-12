(ns co-who.composedb.client
  (:require ["@composedb/client" :refer [ComposeClient]]
            ["@ceramicnetwork/http-client" :refer [CeramicClient]]
            ["did-session" :refer [DIDSession]]
            ["graphql-tag" :as graphql-tag]
            ["@apollo/client" :refer [ApolloClient, ApolloLink, InMemoryCache, Observable]]
            ["../../__generated__/definition.js" :refer [definition]]
            ["graphql" :as graphql]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./auth.cljs" :as a]))

(defonce client (atom {:ceramic (CeramicClient. js/import.meta.env.VITE_CERAMIC_API)
                       :compose (ComposeClient. {:ceramic js/import.meta.env.VITE_CERAMIC_API
                                                 :definition definition})
                       #_(js/Promise. (fn [resolve reject] (resolve) (reject)))
                       :session nil}))

(defonce link (ApolloLink. (fn [operation]
                             (Observable. (fn [observer]
                                        (.then (.execute (:compose @client) operation.query operation.variables)
                                               (fn [result]
                                                 (.next observer result)
                                                 (.complete observer))
                                               (fn [error]
                                                 (.error observer error))))))))

(defonce apollo-client (atom (ApolloClient. {:link link
                                             :assumeImmutableResults true
                                             :cache (InMemoryCache.)})))

(defn exec-query [q vars]
  (when-not (string? q)
    (println "res3: " (graphql-tag/gql (graphql/print q)))
    (println "res3:2:"  q))
  (.query @apollo-client {:query (if (string? q) (graphql-tag/gql q) q) :variables (or vars {})}))

(defn exec-mutation [mutation vars]
  (.mutate @apollo-client {:mutation (graphql-tag/gql mutation) :variables (or vars {})}))

(defn ^:async get-stored-session-string [account-id]
  (let [stored-sessions (sqeave/get-item "codo:ceramic-sessions")]
    (get stored-sessions account-id)))

(defn ^:async get-session-from-string [session-string]
  (.fromSession DIDSession session-string))

(defn  ^:async check-session-expiry [session]
  (let [expiration-date (js/Date. (.-expires session))
        current-date (js/Date.)]
    (if (>= (.getTime current-date) (.getTime expiration-date))
      (do
        (println "The DID session has expired.")
        true)
      (do
        (println "The DID session is still valid.")
        false))))

(defn ^:async get-session []
  (let [account-id (js-await (:account-id @a/auth))
        auth-method (js-await (:auth-method @a/auth))
        resources {:resources (aget (:compose @client) "resources")}
        ;session-string (js-await (get-stored-session-string account-id))
        ]
    (.get DIDSession account-id auth-method resources)
    #_(if (has-session-for account-id resources) #_(or (nil? session-string) (undefined? session-string))

        (do (println "session string: " session-string) (get-session-from-string session-string)))))

(defn ^:async init-clients [r]
  (let [account-id (js-await (:account-id @a/auth))

                                        ;session-token (u/get-item {:key "ceramic-session-dids"})
                                        ;
        session (js-await (.then (get-session) (fn [session]
                                                 (println "got session:" session)
                                                 (.setDID (:compose @client) (aget session "did"))
                                                 (aset (:ceramic @client)  "did" (aget session "did"))
                                                 session)))
        #_session #_(.then (.get DIDSession account-id auth-method {:resources (aget (:compose @client) "resources")}))]

    (println (:compose @client))
    (println "composedb client init OK")
    (println "session: " session)
    (println "dids: " (keys (sqeave/get-item "ceramic-session-dids")))
    ;(u/set-item! "ceramic-session-dids" (assoc (if-let [dids (u/get-item "codo:ceramic-sessions")] dids {}) account-id (.serialize session)))
    session))
