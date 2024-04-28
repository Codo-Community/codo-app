(ns co-who.composedb.auth
  (:require [co-who.evm.client :refer [wallet-client]]
            [co-who.evm.util :as eu]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [mount.core :refer [defstate]]
            [co-who.utils :as u]
            ["did-session" :refer [DIDSession]]
            ["@didtools/pkh-ethereum" :refer [EthereumWebAuth getAccountId]]))

(def auth (atom {}))

(defn init-auth []
  (go
    #_(let [session-str (u/get-item "ceramic:eth_did")
            session (.fromSession ^js DIDSession session-str)]

        (if (or (not session-str)
                (and session.hasSession session.isExpired))))
    (let [ethProvider @wallet-client
          addresses (<p! (.request @wallet-client (clj->js {:method "eth_requestAccounts"})))
          account-id (<p! (getAccountId ethProvider (clj->js (first addresses))))
          auth-method (<p! (EthereumWebAuth.getAuthMethod @wallet-client account-id))]
      (println "ComposeDB auth OK")
      (reset! auth {:addresses addresses :account-id account-id :auth-method auth-method}))))

#_(defstate auth :start  (init-auth))


(comment (init-auth)
         (authenticate-user))

(defn authenticate-user []
  (let [account (eu/request-addresses @wallet-client
                                      (fn [x] (.then (getAccountId @wallet-client (first x)))))
        auth-method (eu/request-addresses @wallet-client
                                          (fn [x] (-> x
                                                      (#(.then (getAccountId @wallet-client (first %))))
                                                      (#(.then (EthereumWebAuth.getAuthMethod @wallet-client %)))
                                                      #_(#(DIDSession.get % )))
                                            ))
        ;;   session (DIDSession.get account-id)
        ]
    #_(println "accc-id: " account-id)
    #_(println "adr: " authMethod)
    [account auth-method]
    ))

(defn get-session [account-id auth-method resources]
  (DIDSession.get account-id auth-method resources))

;; const accountId = await getAccountId(ethProvider, addresses[0])
;; const authMethod = await EthereumWebAuth.getAuthMethod(ethprovider, accountId)

;; const session = await DIDSession.get(accountId, authMethod, { resources: compose.resources})
;; compose.setDID(session.did)
