(ns components.user
  (:require ["solid-js" :refer [onMount createSignal]]
            ["blo" :refer [blo]]
            ["./blueprint/tooltip.cljs" :as tt]
            ["./blueprint/button.cljs" :as b]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../composedb/client.cljs" :as cdb]
            ["../composedb/auth.cljs" :as cda]
            ["../utils.cljs" :as utils]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]]
            ["flowbite" :refer [initDropdowns]]
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
         (fn [r] (.then (cdb/init-clients)
                   (fn [r]
                     (println "wc: r: " r)
                     (load-viewer-user #(t/add! ctx (conj % {:user/session (-> (:compose @cli/client) :did :_id)}) {:replace [:component/id :header :user]})))))))

(defc User [this {:user/keys [id firstName ethereum-address]}]
  (do
    (onMount #(initDropdowns))
    #jsx [:div {:class "flex items-center text-black flex items-center justify-center rounded-md"
                :data-dropdown-toggle (:data-dropdown-toggle props)}
          [b/button {:extra-class "!h-10 !w-10 !p-0 !border-0"
                     :img-class ""
                     :img (blo (ethereum-address))}]
          #_(Show show-name
                  [:div {:class "px-2"}]
                  (if-not (= first-name "")
                    (str first-name " " last-name)
                    (subs (str ethereum-address) 0 8)))])
  #_[tt/tooltip {:id "header-user-tt"
                 :content (ethereum-address)}])

(def ui-user (comp/comp-factory User AppContext))
