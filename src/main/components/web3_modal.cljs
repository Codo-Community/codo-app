(ns main.components.web3-modal
  (:require ["solid-js" :refer [createSignal]]
            ["../comp.cljs" :as comp]
            ["@web3modal/wagmi" :refer [createWeb3Modal]]
            ["../evm/walletconnect.cljs" :refer [config project-id]]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Web3Modal [this {:keys [] :or {}}]
  (let [modal (createWeb3Modal {:wagmiConfig config
                                :projectId project-id})
        [local setLocal] (createSignal {:open? false})]
    #jsx [:button {:onClick (fn [e]
                              (if (:open? (local))
                                (modal.close)
                                (modal.open)))} "Open Web3Modal"]))

(def ui-web3-modal (comp/comp-factory Web3Modal AppContext))
