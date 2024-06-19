(ns co-who.composedb.auth
  (:require ["../evm/client.cljs" :refer [wallet-client]]
            ["../evm/util.cljs" :as eu]
            ["did-session" :refer [DIDSession]]
            ["@didtools/pkh-ethereum" :refer [EthereumWebAuth getAccountId]]))

(def addresses-resolver (atom nil))
(def account-id-resolver (atom nil))
(def auth-method-resolver (atom nil))

(def auth (atom nil))

(defn reset-auth! []
  (reset! auth {:addresses (js/Promise. (fn [resolve _] (reset! addresses-resolver resolve)))
                :account-id (js/Promise. (fn [resolve _] (reset! account-id-resolver resolve)))
                :auth-method (js/Promise. (fn [resolve _] (reset! auth-method-resolver resolve)))}))

(defn ^:async init-auth []
  #_(let [session-str (u/get-item "ceramic:eth_did")
          session (.fromSession ^js DIDSession session-str)]

      (if (or (not session-str)
              (and session.hasSession session.isExpired))))
  (reset-auth!)

  (let [ethProvider @wallet-client
        addresses (js-await (.request @wallet-client {:method "eth_requestAccounts"}))
        account-id (js-await (getAccountId ethProvider (first addresses)))
        auth-method (js-await (EthereumWebAuth.getAuthMethod @wallet-client account-id))]
    (@addresses-resolver addresses)
    (@account-id-resolver account-id)
    (@auth-method-resolver auth-method)
    (println "ComposeDB auth OK: " account-id)
    #_(reset! auth {:addresses addresses :account-id account-id :auth-method auth-method})
    (js/Promise.resolve @auth)))

#_(defstate auth :start  (init-auth))


(comment (init-auth)
         (authenticate-user))

(defn authenticate-user []
  (let [account (eu/request-addresses @wallet-client
                                      (fn [x]
                                        (.then (getAccountId @wallet-client (first x)))))
        auth-method (eu/request-addresses @wallet-client
                                          (fn [x] (-> x
                                                      (#(.then (getAccountId @wallet-client (first %))))
                                                      (#(.then (EthereumWebAuth.getAuthMethod @wallet-client %)))
                                                      #_(#(DIDSession.get % )))
                                            ))
        ;;   session (DIDSession.get account-id)
        ]
    (println "accc-id: " account)
    #_(println "adr: " authMethod)
    [account auth-method]
    ))

(defn get-session [account-id auth-method resources]
  (DIDSession.get account-id auth-method resources))

;; const accountId = await getAccountId(ethProvider, addresses[0])
;; const authMethod = await EthereumWebAuth.getAuthMethod(ethprovider, accountId)

;; const session = await DIDSession.get(accountId, authMethod, { resources: compose.resources})
;; compose.setDID(session.did)
