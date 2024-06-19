(ns co-who.composedb.client
  (:require ["@composedb/client" :refer [ComposeClient]]
            ["@ceramicnetwork/http-client" :refer [CeramicClient]]
            ["did-session" :refer [DIDSession]]
            ["../../__generated__/definition.js" :refer [definition]]
            ["./auth.cljs" :as a]))

(def client (atom {:ceramic (CeramicClient. "http://localhost:7007")
                   :compose (ComposeClient. {:ceramic "http://localhost:7007"
                                             :definition definition})
                   #_(js/Promise. (fn [resolve reject] (resolve) (reject) ))
                   :session nil}))

(defn ^:async await-session [compose auth]
  (let [account-id (js-await (:account-id @a/auth))
        auth-method (js-await (:auth-method @a/auth))]
    (.get DIDSession account-id auth-method  {:resources (aget compose "resources")})))

(defn ^:async init-clients []
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
