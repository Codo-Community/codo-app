(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy Suspense]]
            ["@wagmi/core" :refer [watchClient watchPublicClient watchConnections]]
            ["@didtools/pkh-ethereum" :refer [getAccountId]]
            ["./router.cljs" :as r]
            ["./evm/client.cljs" :as ec]
            ["./composedb/data_feed.cljs" :as cdf]
            ["./composedb/events.cljs" :as events]
            ["./composedb/client.cljs" :as cli]
            ["./composedb/util.cljs" :as cu]
            ["./evm/walletconnect.cljs" :refer [config]]
            ["./components/user.cljs" :as u]
            ["./comp.cljs" :as comp :refer [Comp]]
            ["./transact.cljs" :as t]
            ["./Context.cljs" :as context :refer [AppContext]]
            ["flowbite" :as fb]
            ["@tanstack/solid-query" :refer [QueryClientProvider QueryClient]])
  (:require-macros [comp :refer [defc]]))

(defonce queryClient (QueryClient.))

(defn init-auth-silent []
  )

(defc Root [this {:keys []}]
  (let [ctx  (context/init-context)]
    (onMount #(do
                (println "init")
                #_(cdf/init-listeners ctx events/handle)
                (.then  (ec/init-clients) (fn [res] (println "init-clients" res)))
                (reset! ec/unwatch-wallet (watchClient config  {:onChange (fn [client]
                                                                            (println "change wallet: " client)
                                                                            (reset! ec/wallet-client client))}))
                (reset! ec/unwatch-public (watchPublicClient config  {:onChange (fn [client]
                                                                                  (println "change wallet: " client)
                                                                                  (reset! ec/public-client client))}))
                (reset! ec/unwatch-connections (watchConnections config  {:onChange (fn [connections]
                                                                                      (println "change connections: " connections)
                                                                                      (let [{:keys [accounts chainId]} (first connections)]
                                                                                        (println "accounts: " accounts)
                                                                                        (if (empty? accounts)
                                                                                          (do
                                                                                            (t/add! ctx {:viewer/id 0
                                                                                                         :viewer/session nil})
                                                                                            (t/add! ctx {:user/id 0 :user/ethereum-address "0x0" :user/session nil} {:replace [:component/id :header :user]})
                                                                                            (-> @cli/apollo-client :cache (.reset)))
                                                                                          (.then (getAccountId @ec/wallet-client (first accounts))
                                                                                                 (fn [acc-id]
                                                                                                   (.then (cu/has-session-for acc-id (aget (:compose @cli/client) "resources"))
                                                                                                          (fn [session]
                                                                                                            (when session
                                                                                                              (u/init-auth {:store (first ctx) :setStore (second ctx)})))))))))}))
                (fb/initFlowbite)))
    #jsx [AppContext.Provider {:value ctx}
          [QueryClientProvider {:client queryClient}
           [r/ui-router]]]))

(def ui-root (comp/comp-factory Root AppContext))
