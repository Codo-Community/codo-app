(ns index
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy Suspense]]
            ["solid-js/web" :refer [render]]
            ["@wagmi/core" :refer [watchClient watchPublicClient watchConnections getConnections]]
            ["@didtools/pkh-ethereum" :refer [getAccountId]]
            ["./router.cljs" :as r]
            ["./query_client.cljs" :refer [queryClient]]
            ["./evm/client.cljs" :as ec]
            #_["./composedb/data_feed.cljs" :as cdf]
            #_["./composedb/events.cljs" :as events]
            ["./composedb/client.cljs" :as cli]
            ["./composedb/util.cljs" :as cu]
            ["./evm/walletconnect.cljs" :refer [config]]
            ["./components/user.cljs" :as u]
            ["./Context.cljs" :as context :refer [AppContext ConnectionContext]]
            ["./request/lib.cljs" :as req]
            ["flowbite" :as fb]
            ["@w3t-ab/sqeave" :as sqeave]
            ["solid-devtools"]
            ["@unocss/reset/tailwind.css"]
            ["virtual:uno.css"]
            #_["@tanstack/solid-query" :refer [QueryClientProvider]])
  (:require-macros [sqeave :refer [defc]]))

(defc Root [this {:keys [] :ctx (sqeave/init-ctx! AppContext)}]
  (let [;ctx  (context/init-context)
        connection-ctx (let [[connections setConnections] (createSignal (getConnections config))]
                         (.then  (ec/init-clients) (fn [res]
                                            (println "init-clients" res)
                                            (req/init-request-client)))
                         {:connections connections
                          :setConnections setConnections})]
    (onMount #(do
                #_(cdf/init-listeners ctx events/handle)

                #_(reset! ec/unwatch-wallet (watchClient config  {:onChange (fn [client]
                                                                            (println "change wallet: " client)
                                                                            (reset! ec/wallet-client client))}))
                #_(reset! ec/unwatch-public (watchPublicClient config  {:onChange (fn [client]
                                                                                  (println "change wallet: " client)
                                                                                  (reset! ec/public-client client))}))
                #_(reset! ec/unwatch-connections (watchConnections config  {:onChange (fn [connections]
                                                                                      (println "change connections: " connections)
                                                                                      ((:setConnections connection-ctx) connections)
                                                                                      (let [{:keys [accounts chainId]} (first connections)]
                                                                                        (println "accounts: " accounts)
                                                                                        (if (empty? accounts)
                                                                                          (do
                                                                                            (sqeave/add! ctx {:viewer/id 0
                                                                                                              :viewer/session nil})
                                                                                            (sqeave/add! ctx {:user/id 0 :user/ethereum-address "0x0" :user/session nil} {:replace [:component/id :header :user]})
                                                                                            (-> @cli/apollo-client :cache (.reset)))
                                                                                          (.then (getAccountId @ec/wallet-client (first accounts))
                                                                                                 (fn [acc-id]
                                                                                                   (.then (cu/has-session-for acc-id (aget (:compose @cli/client) "resources"))
                                                                                                          (fn [session]
                                                                                                            (when session
                                                                                                              (u/init-auth {:store (first ctx)
                                                                                                                            :setStore (second ctx)})))))))))}))
                (fb/initFlowbite)))
    #jsx [AppContext.Provider {:value this.ctx}
          [ConnectionContext.Provider {:value connection-ctx}
           [r/Router]
           #_[QueryClientProvider {:client queryClient}
            ]]]))

(let [e (js/document.getElementById "root")]                                ; 5
  (set! (aget e :innerHTML) "")
  (render Root e))
