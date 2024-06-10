(ns co-who.composedb.client
  (:require ["@composedb/client" :refer [ComposeClient]]
            ["@ceramicnetwork/http-client" :refer [CeramicClient]]
            ["did-session" :refer [DIDSession]]
            ;["./composite.cljs" :as c :refer [composite]]
            ["../../__generated__/definition.js" :refer [definition]]
            ["./auth.cljs" :as a]))

#_(def composite (js/await
                  (->
                   (js/fetch "http://localhost:5173/runtime-composite.json")
                   (.then (fn [response] (.json response))))))

#_(def compositea {:models {:SimpleProfile {:id "kjzl6hvfrbw6c8wlx340w9nmgfrtwvkkmkf1s4x3lrrtv8gu7sd8relzobvputu"
                                           :accountRelation {:type "single"}}}
                :objects {:SimpleProfile {:displayName {:type "string" :required true}}}
                :enums {}
                :accountData {:simpleProfile {:type "node" :name "SimpleProfile"}}})

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
                         #_(js/console.log "session did:" compose.context)
                         (.setDID (:compose @client) (aget session "did"))
                         (aset (:ceramic @client)  "did" (aget session "did"))
                         session))]
    #_(.resolve (:ceramic @client) ceramic)
    #_(.resolve (:compose @client) compose)
    #_(reset! client {:ceramic (js/Promise.resolve ceramic)
                      :compose (js/Promise.resolve compose)})
    (js/Promise.resolve session)))

(comment
  (do
    (a/init-auth)
    (init-clients))

  (js/console.log (:session @client))
  (let [c (:ceramic
           @client)]
    (js/console.log c))

  (js/console.log (aget (:compose @client) "runtime"))

  (js/console.log (:ceramic @client))

  (let [compose (ComposeClient. (clj->js {:ceramic "http://localhost:7007"
                                          :definition (clj->js composite)}))]
    (DIDSession.get (:account-id @a/auth) (:auth-method @a/auth) (clj->js {:resources (aget compose "resources")})))

  (def session
    (let [compose (ComposeClient. (clj->js {:ceramic "http://localhost:7007"
                                            :definition (clj->js composite)}))
          [account-id auth-method] (a/authenticate-user)
          session (.then account-id (fn [a-id]
                                      (.then auth-method (fn [au-m]
                                                           (.then (a/get-session a-id au-m (clj->js  {:resources (aget compose "resources")}))
                                                                  (fn [r] r))))))]
      (println "session: " session)
      (compose.setDID session.did)
      session)))

                                       ;compose.setDID(session.did)

;; const QUERY = `
;;   query Test($id: ID!) {
;;     getUser(id: $id) {
;;       id
;;       name
;;     }
;;   }
;; `;

;; client
;;   .query(QUERY, { id: 'test' })
;;   .toPromise()
;;   .then(result => {
;;     console.log(result); // { data: ... }
;;   });
