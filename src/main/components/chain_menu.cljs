(ns main.components.chain-menu
  (:require ["../comp.cljs" :as comp]
            ["solid-js" :refer [Show onMount createSignal]]
            ["./blueprint/dropdown.cljs" :as d]
            ["../Context.cljs" :refer [AppContext]]
            ["../evm/client.cljs" :as ec]
            ["../evm/util.cljs" :as eu]
            ["viem/chains" :refer [sepolia hardhat]])
  (:require-macros [comp :refer [defc]]))

(def id-to-chain {(:id sepolia) sepolia
                  (:id hardhat) hardhat})

(def chains {(:name sepolia) sepolia
             (:name hardhat) hardhat})

(defn switch-chain [setLocal]
  (fn [r]
    (let [id (if (not (int? r)) (js/parseInt r 16) r)]
      (setLocal id)
      (ec/init-clients ec/wallet-client ec/public-client (get id-to-chain id)))))

(defc ChainMenu [this {:keys []}]
  (let [[local setLocal] (createSignal (or (:id ec/wallet-client.chain)
                                           (:id (first chains))))
        switch (switch-chain setLocal)]
    (onMount (fn []
               (.then (eu/get-chain) switch)
               (eu/add-chain-changed switch)))
    #jsx [d/dropdown-select {:& {:items #(mapv (fn [c] {:id (:id c)
                                                        :value (:name c)})
                                               (vals chains))
                                 :on-change #(.then (.switchChain @ec/wallet-client {:id (get-in chains [(-> % :target :value) :id])}))
                                 :selected local}}]))

(def ui-chain-menu (comp/comp-factory ChainMenu AppContext)
  )
