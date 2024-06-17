(ns main.components.chain-menu
  (:require ["../comp.cljs" :as comp]
            ["solid-js" :refer [Show onMount createSignal]]
            ["./blueprint/dropdown.cljs" :as d]
            ["./blueprint/icons/web3.cljs" :as wi]
            ["../Context.cljs" :refer [AppContext]]
            ["../evm/client.cljs" :as ec]
            ["../evm/util.cljs" :as eu]
            ["flowbite" :refer [initDropdowns initTooltips]]
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
               (initDropdowns) (initTooltips)
               (.then (eu/get-chain) switch)
               (eu/add-chain-changed switch)))
    #jsx [:div {}
          [:button {:class "flex gap-2 block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white items-center rounded-md rounded border border-gray-300 dark:border-gray-600 text-gray-600 dark:text-gray-300 dark:hover:text-white"

                    :data-dropdown-toggle "chain-menu"}
           [:div {:class "w-7 h-7 p-1 flex items-center justify-center"}
            [:img {:class ""
                   :draggable false
                   :onDragStart nil
                   :src (get wi/icons (local))}]]
           (-> (get id-to-chain (local)) :name)]
          [d/dropdown {:& {:id "chain-menu"
                           :items #(mapv (fn [c] {:id (:id c)
                                                  :value (:name c)
                                                  :icon (get wi/icons (:id c))})
                                         (vals chains))
                           :on-change #(.then (.switchChain @ec/wallet-client (get-in chains [(-> % :target :value) :id])))
                           :selected local}}]]))

(def ui-chain-menu (comp/comp-factory ChainMenu AppContext))
