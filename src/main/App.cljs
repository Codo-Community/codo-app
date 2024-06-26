(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["./router.cljs" :as r]
            ["./evm/client.cljs" :as ec]
            ["./composedb/data_feed.cljs" :as cdf]
            ["./composedb/events.cljs" :as events]
            ["./comp.cljs" :as comp :refer [Comp]]
            ["./Context.cljs" :as context :refer [AppContext]]
            ["flowbite" :as fb]
            ["@tanstack/solid-query" :refer [QueryClientProvider QueryClient]])
  (:require-macros [comp :refer [defc]]))

(defonce queryClient (QueryClient.))

(defc Root [this {:keys []}]
  (let [ctx  (context/init-context)]
    (onMount #(do
                ;; TODO: fetch abi here
                (println "init")
                #_(cdf/init-listeners ctx events/handle)
                (ec/init-clients)
                (fb/initFlowbite)))
    #jsx [AppContext.Provider {:value ctx}
          [QueryClientProvider {:client queryClient}
           #jsx [r/ui-router]]]))

(def ui-root (comp/comp-factory Root AppContext))
