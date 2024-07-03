(ns components.user
  (:require ["solid-js" :refer [onMount createSignal createResource createMemo]]
            ["blo" :refer [blo]]
            ["./blueprint/tooltip.cljs" :as tt]
            ["./blueprint/button.cljs" :as b]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../evm/client.cljs" :as ec :refer [wallet-client]]
            ["../evm/gc_passport.cljs" :as gcp]
            ["../composedb/auth.cljs" :as cda]
            ["../utils.cljs" :as utils]
            ["../composedb/client.cljs" :as cli]
            ["../composedb/util.cljs" :as cu]
            ["../Context.cljs" :refer [AppContext]]
            ["./alert.cljs" :as alert]
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
      account { id }
    }
  }
}
")

(defn load-viewer-user [ctx]
  (cu/execute-gql-query ctx query-from-acc {}
                        (fn [response]
                          (let [a (println "res" response)

                                res (conj (utils/nsd (-> response :viewer :user) :user)
                                          {:user/ethereum-address (nth (string/split (-> response :viewer :id) ":") 4)})

                                res (if-not (:user/id res)
                                      (conj res {:user/id (js/crypto.randomUUID)})
                                      res)

                                res (conj res {:user/session (-> (:compose @cli/client) :did :_id)})]
                            (t/add! ctx {:viewer/id 0
                                         :viewer/user [:user/id (:user/id res)]
                                         :viewer/did (-> response :viewer :id)
                                         :viewer/session (-> (:compose @cli/client) :did :_id)})
                            (t/add! ctx res {:replace [:component/id :header :user]})))))

(defn ^:async init-auth [ctx]
  (.catch (.then (cda/init-auth)
                 (fn [r]
                   (.then (cli/init-clients)
                          (fn [r]
                            (load-viewer-user ctx)
                            (-> @cli/apollo-client :cache (.reset))))))
          (alert/alert-error ctx)))

(defc User [this {:user/keys [id name ethereum-address {account [:id]} avatar passport-score]
                  :or {id (utils/uuid) name "" avatar nil passport-score 0 ethereum-address "0x0"}}]
  (let [[ens {:keys [mutate refetch]}] (createResource ethereum-address (fn ^:async [source  {:keys [value refetching]}]
                                                                          (.then (gcp/get-passport-score (ethereum-address))
                                                                                 (fn [score]
                                                                                   (t/add! ctx {:user/id (id)
                                                                                                :user/passport-score score} {:after (fn []
                                                                                                                                      (initDropdowns) (initTooltips))})))
                                                                          (.then (.getEnsName @ec/mainnet-client {:address source})
                                                                                 (fn [name]
                                                                                   (.then (.getEnsAvatar @ec/mainnet-client {:name (normalize name)})
                                                                                          (fn [avatar]
                                                                                            ((fn [score]
                                                                                               (let [data {:user/id (id) :user/name name :user/avatar avatar}]
                                                                                                 (t/add! ctx data {:after (fn []
                                                                                                                            (initDropdowns) (initTooltips))}))))))))))
        score-colors (fn [score] (if (> score 19)
                                   "bg-green-400"
                                   (if (> score 10)
                                     "bg-yellow-400"
                                     "bg-red-400")))]
    (createMemo (fn [] (when (id) (initDropdowns) (initTooltips))))
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
                       :content #jsx [:div {:class "px-2 dark:text-white text:sm"}
                                      (if (or (nil? (name)) (= (name) ""))
                                        (ethereum-address)
                                        (name))]}]]))

(def ui-user (comp/comp-factory User AppContext))
