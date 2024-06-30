(ns components.user
  (:require ["solid-js" :refer [onMount createSignal createResource]]
            ["blo" :refer [blo]]
            ["./blueprint/tooltip.cljs" :as tt]
            ["./blueprint/button.cljs" :as b]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../evm/client.cljs" :as ec :refer [wallet-client]]
            ["../composedb/client.cljs" :as cdc]
            ["../composedb/auth.cljs" :as cda]
            ["../utils.cljs" :as utils]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]]
            ["flowbite" :refer [initDropdowns initTooltips]]
            ["viem/ens" :refer [normalize]]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

(def query-from-acc "query {
  viewer {
    id
    user {
      id
      firstName
    }
  }
}
")

(defn load-viewer-user [f]
  (-> (.executeQuery (:compose @cli/client) query-from-acc)
      (.then (fn [response]
               (println "load-viewer-user: response: " response)
               (let [res (conj (utils/nsd (-> response :data :viewer :user) :user)
                               {:user/ethereum-address (nth (string/split (-> response :data :viewer :id) ":") 4)})
                     res (if-not (:user/id res)
                           (conj res {:user/id (js/crypto.randomUUID)})
                           res)]
                 (f res))))))

(defn ^:async init-auth [ctx]
  (.then (cda/init-auth)
         (fn [r]
           (.then (cdc/init-clients)
                  (fn [r]
                    (load-viewer-user #(t/add! ctx (conj % {:user/session (-> (:compose @cli/client) :did :_id)}) {:replace [:component/id :header :user]})))))))

(defc User [this {:user/keys [id name ethereum-address avatar]}]
  (let [[ens {:keys [mutate refetch]}] (createResource ethereum-address (fn ^:async [source  {:keys [value refetching]}]
                                                                          (.then  (.getEnsName @ec/mainnet-client {:address source})
                                                                                  (fn [name]
                                                                                    (.then (.getEnsAvatar @ec/mainnet-client {:name (normalize name)})
                                                                                           (fn [avatar]
                                                                                             (t/add! ctx {:user/id (id) :user/name name :user/avatar avatar})
                                                                                             (initDropdowns) (initTooltips)
                                                                                             {:name name :avatar avatar}))))))]
    (onMount (do (initDropdowns) (initTooltips)))
    #jsx [:div {}
          [:div {:class "flex items-center text-black flex items-center justify-center rounded-md"
                 :data-dropdown-toggle (:data-dropdown-toggle props)
                 :data-tooltip-target (str "user-tooltip-" (ethereum-address))}
           [b/button {:extra-class "!h-10 !w-10 !p-0 !border-0"
                      :img-class ""
                      :img (or (avatar) (blo (ethereum-address)))}]]
          [tt/tooltip {:id (str "user-tooltip-" (ethereum-address))
                       :content #jsx [:div {:class "px-2 dark:text-white"}
                                      (or (str (name))
                                          (subs (ethereum-address) 0 8))]}]]))

(def ui-user (comp/comp-factory User AppContext))
