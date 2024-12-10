(ns main.components.web3-modal
  (:require ["solid-js" :refer [createSignal Switch Match onMount]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./blueprint/button.cljs" :as b]
            ["@web3modal/wagmi" :refer [createWeb3Modal]]
            ["@wagmi/core" :refer [getConnections]]
            ["../evm/walletconnect.cljs" :refer [config project-id]])
  (:require-macros [sqeave :refer [defc]]))

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
