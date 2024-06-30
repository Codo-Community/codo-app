(ns co-who.composedb.client
  (:require ["@composedb/client" :refer [ComposeClient]]
            ["@ceramicnetwork/http-client" :refer [CeramicClient]]
            ["did-session" :refer [DIDSession]]
            ["graphql-tag" :as graphql-tag]
            ["@apollo/client" :refer [ApolloClient, ApolloLink, InMemoryCache, Observable]]
            ["../../__generated__/definition.js" :refer [definition]]
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
                                             :cache (InMemoryCache.)})))

(defn exec-query [q vars]
  (.query @apollo-client {:query (graphql-tag/gql q) :variables (or vars {})}))

(defn exec-mutation [mutation vars]
  (.mutate @apollo-client {:mutation (graphql-tag/gql mutation) :variables (or vars {})}))

(defn ^:async await-session []
  (let [account-id (js-await (:account-id @a/auth))
        auth-method (js-await (:auth-method @a/auth))]
    (.get DIDSession account-id auth-method  {:resources (aget (:compose @client) "resources")})))

(defn ^:async init-clients [r]
  (println "got acc-id etc." r)
  (let [account-id (js-await (:account-id @a/auth))
        auth-method (js-await (:auth-method @a/auth))
        session (.then (.get DIDSession account-id auth-method {:resources (aget (:compose @client) "resources")})
                       (fn [session]
                         (.setDID (:compose @client) (aget session "did"))
                         (aset (:ceramic @client)  "did" (aget session "did"))
                         session))]
    (println (:compose @client))
    (println "composedb client init OK")
    (js/Promise.resolve session)))
