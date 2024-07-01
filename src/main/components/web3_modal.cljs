(ns main.components.web3-modal
  (:require ["solid-js" :refer [createSignal Switch Match onMount]]
            ["../comp.cljs" :as comp]
            ["./blueprint/button.cljs" :as b]
            ["@web3modal/wagmi" :refer [createWeb3Modal]]
            ["@wagmi/core" :refer [getConnections]]
            ["../evm/walletconnect.cljs" :refer [config project-id]]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Web3Modal [this {:keys [] :or {}}]
  (let [modal (createWeb3Modal {:wagmiConfig config
                                :enableAnalytics false
                                :enableEmail false
                                :enableOnRamp false
                                :projectId project-id})
        [local setLocal] (createSignal {:open? false
                                        :connected? false})]
    (onMount (fn [] (setLocal (assoc (local) :connected? (not (empty? (getConnections config)))))))
    #jsx [:div {}
          [Switch {}
           [Match {:when (not (:connected? (local)))}
            [b/button {:title "Connect"
                       :on-click #(if (:open? (local))
                                    (modal.close)
                                    (modal.open))}]]
           [Match {:when (:connected? (local))}
            [b/button {:title "Open"
                       :on-click #(if (:open? (local))
                                    (modal.close)
                                    (modal.open))}]]]]))

(def ui-web3-modal (comp/comp-factory Web3Modal AppContext))
