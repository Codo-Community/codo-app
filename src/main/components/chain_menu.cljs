(ns main.components.chain-menu
  (:require ["../comp.cljs" :as comp]
            ["solid-js" :refer [Show onMount createSignal]]
            ["./blueprint/dropdown.cljs" :as d]
            ["./blueprint/button.cljs" :as b]
            ["./blueprint/icons/web3.cljs" :as wi]
            ["../Context.cljs" :refer [AppContext]]
            ["../evm/client.cljs" :as ec]
            ["../evm/util.cljs" :as eu]
            ["../utils.cljs" :as u]
            ["flowbite" :refer [initDropdowns initTooltips]]
            ["viem/chains" :refer [sepolia hardhat mainnet polygon arbitrum]])
  (:require-macros [comp :refer [defc]]))

(def chain-vec (if js/import.meta.env.PROD
              [mainnet polygon arbitrum]
              [sepolia hardhat]))

(def id-to-chain (into {} (mapv (fn [c] {(:id c) c}) chain-vec)) #_{(:id sepolia) sepolia
                  (:id hardhat) hardhat})

(def chains (into {} (mapv (fn [c] {(:name c) c}) chain-vec)) #_{(:name sepolia) sepolia
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
               (initDropdowns) (initTooltips)
               (.then (eu/get-chain) switch)
               (eu/add-chain-changed switch)))
    #jsx [:div {:data-dropdown-toggle "chain-menu"}
          [b/button {:extra-class "!h-10 lt-md:hidden"
                     :title (-> (get id-to-chain (local)) :name)
                     :img (str u/ipfs-folder (get wi/icons (local)))}
           [:div {:class "w-5 h-5 i-tabler-chevron-down"}]]
          [d/dropdown {:& {:id "chain-menu"
                           :items #(mapv (fn [c] {:id (:id c)
                                                  :value (:name c)
                                                  :img (str u/ipfs-folder (get wi/icons (:id c)))})
                                         (vals chains))
                           :on-change #(do (println %  " " (get-in chains [(-> % :target :textContent) :id]))
                                         (.then (.switchChain @ec/wallet-client {:id (get-in chains [(-> % :target :textContent) :id])})))
                           :selected local}}]]))

(def ui-chain-menu (comp/comp-factory ChainMenu AppContext))
