(ns co-who.composedb.auth
  (:require ["../evm/client.cljs" :as ec]
            ["@didtools/pkh-ethereum" :refer [EthereumWebAuth getAccountId]]))

(def addresses-resolver (atom nil))
(def account-id-resolver (atom nil))
(def auth-method-resolver (atom nil))

(def auth (atom nil))

(defn reset-auth! []
  (reset! auth {:account-id (js/Promise. (fn [resolve _] (reset! account-id-resolver resolve)))
                :auth-method (js/Promise. (fn [resolve _] (reset! auth-method-resolver resolve)))}))

(defn ^:async init-auth []

  (reset-auth!)

  (.then (ec/init-clients)
         (fn [wc]
           (println "got client: " wc)
           (.then (getAccountId wc (-> wc :account :address))
                  (fn [account-id]
                    (@account-id-resolver account-id)
                    (.then (EthereumWebAuth.getAuthMethod wc account-id)
                           (fn [auth-method]
                             (@auth-method-resolver auth-method)
                             (println "ComposeDB auth OK: " account-id)
                             [account-id auth-method])))))))
