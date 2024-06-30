(ns components.user
  (:require ["solid-js" :refer [onMount createSignal createResource]]
            ["blo" :refer [blo]]
            ["./blueprint/tooltip.cljs" :as tt]
            ["./blueprint/button.cljs" :as b]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../evm/client.cljs" :as ec :refer [wallet-client]]
            ["../evm/gc_passport.cljs" :as gcp]
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
  (.then (cli/exec-query query-from-acc)
         (fn [response]
           (println "load-viewer-user: response: " response)
           (let [res (conj (utils/nsd (-> response :data :viewer :user) :user)
                           {:user/ethereum-address (nth (string/split (-> response :data :viewer :id) ":") 4)})
                 res (if-not (:user/id res)
                       (conj res {:user/id (js/crypto.randomUUID)})
                       res)]
             (f res)))))

(defn ^:async init-auth [ctx]
  (.then (cda/init-auth)
         (fn [r]
           (.then (cdc/init-clients)
                  (fn [r]
                    (load-viewer-user #(t/add! ctx (conj % {:user/session (-> (:compose @cli/client) :did :_id)}) {:replace [:component/id :header :user]})))))))

(defc User [this {:user/keys [id name ethereum-address avatar passport-score]}]
  (let [[ens {:keys [mutate refetch]}] (createResource ethereum-address (fn ^:async [source  {:keys [value refetching]}]
                                                                          (.then  (.getEnsName @ec/mainnet-client {:address source})
                                                                                  (fn [name]
                                                                                    (.then (.getEnsAvatar @ec/mainnet-client {:name (normalize name)})
                                                                                           (fn [avatar]
                                                                                             (.then  (gcp/get-passport-score (ethereum-address))
                                                                                                     (fn [score]
                                                                                                       (let [data {:user/id (id) :user/name name :user/avatar avatar
                                                                                                                   :user/passport-score score}]
                                                                                                         (t/add! ctx data {:after (fn []
                                                                                                                                    (initDropdowns) (initTooltips)
                                                                                                                                    data)}))))))))))
        score-colors (fn [score] (if (> score 19)
                                   "bg-green-400"
                                   (if (> score 10)
                                     "bg-yellow-400"
                                     "bg-red-400")))]
    (onMount (do (initDropdowns) (initTooltips)))
    #jsx [:div {}
          [:div {:class "flex items-center text-black flex items-center justify-center rounded-md relative"
                 :data-dropdown-toggle (:data-dropdown-toggle props)
                 :data-tooltip-target (str "user-tooltip-" (ethereum-address))}
           [:span {:class (str "absolute inline-flex items-center justify-center w-4 h-4 text-xs font-bold text-white dark:text-white
                                rounded-full -top-1 -left-1.5 border-zinc-400 border-1 " (score-colors (passport-score)))}]
           [b/button {:extra-class "!h-10 !w-10 !p-0 !border-0"
                      :img-class ""
                      :img (or (avatar) (blo (ethereum-address)))}]]
          [tt/tooltip {:id (str "user-tooltip-" (ethereum-address))
                       :content #jsx [:div {:class "px-2 dark:text-white"}
                                      (or (str (name))
                                          (subs (ethereum-address) 0 8))]}]]))

(def ui-user (comp/comp-factory User AppContext))
