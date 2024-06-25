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
            ["../evm/walletconnect.cljs" :refer [chains]]
            ["flowbite" :refer [initDropdowns initTooltips]]
            )
  (:require-macros [comp :refer [defc]]))

(def id-to-chain (into {} (mapv (fn [c] {(:id c) c}) chains)))
(def chain-map (into {} (mapv (fn [c] {(:name c) c}) chains)))

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
                                         chains)
                           :on-change #(.then (.switchChain @ec/wallet-client {:id (get-in chain-map [(-> % :target :textContent) :id])}))
                           :selected local}}]]))

(def ui-chain-menu (comp/comp-factory ChainMenu AppContext))
