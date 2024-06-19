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
          [:button {:class "flex gap-2 block px-2 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white h-7
                            items-center rounded-md rounded border border-gray-300 dark:border-gray-600 text-gray-600 dark:text-gray-300 dark:hover:text-white"
                    :data-dropdown-toggle "chain-menu"}
           [:div {:class "flex items-center justify-center"}
            [:img {:class "w-6 h-6 p-1"
                   :draggable false
                   :onDragStart nil
                   :src (get wi/icons (local))}]]
           (-> (get id-to-chain (local)) :name)
           [:div {:class "w-5 h-5 i-tabler-chevron-down"}]]
          [d/dropdown {:& {:id "chain-menu"
                           :items #(mapv (fn [c] {:id (:id c)
                                                  :value (:name c)
                                                  :img (get wi/icons (:id c))})
                                         (vals chains))
                           :on-change #(do (println (get-in chains [(-> % :target :text) :id])) (.then (.switchChain @ec/wallet-client {:id (get-in chains [(-> % :target :text) :id])})))
                           :selected local}}]]))

(def ui-chain-menu (comp/comp-factory ChainMenu AppContext))
