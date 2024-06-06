(ns main.components.wizards.new-project.contract-step
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal createResource]]
            ["solid-spinner" :as spinner]
            ["../../../evm/client.mjs" :as ec]
            ["../../../evm/lib.mjs" :as el]
            ["../../../evm/util.mjs" :as eu]
            ["../../../utils.mjs" :as u]
            ["../../../comp.mjs" :as comp]
            ["../../../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn  ^:async fetch-abi []
  (let [res (js-await (js/fetch "http://localhost:3000/abi/Project.json"))
        [account] (js-await (.getAddresses ec/wallet-client) #_(eu/request-addresses ec/wallet-client #(first %)))
        data (js-await ((aget res "json")))
        asd (js-await (el/deploy-contract ec/wallet-client (:abi data) account (:bytecode data)))]))

(defc ContractStep [this {:project/keys [id contract] :as data}]
  (let [;[contract-data] (createResource fetch-abi)
        ]
    (onMount fetch-abi)
    #jsx [:div {}
          [Show {:when (and (not (nil? (contract)))
                            (not (u/uuid? (contract)))) :fallback #jsx [:div {}
                                                                        [:span {:class "flex gap-4 items-center"}
                                                                         "Deploying contract "
                                                                         [spinner/TailSpin]]]}
           "Project Contract Deployed!"]]))

(def ui-contract-step (comp/comp-factory ContractStep AppContext))
