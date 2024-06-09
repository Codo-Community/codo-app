(ns components.user
  (:require ["blo" :refer [blo]]
            ["./blueprint/tooltip.jsx" :as tt]
            ["./blueprint/button.jsx" :as b]
            ["../comp.mjs" :as comp]
            ["../transact.mjs" :as t]
            ["../utils.mjs" :as utils]
            ["../composedb/client.mjs" :as cli]
            ["../Context.mjs" :refer [AppContext]]
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
               (js/console.log "response: " response)
               (let [res (conj (utils/nsd (-> response :data :viewer :user) :user)
                               {:user/ethereum-address (nth (string/split (-> response :data :viewer :id) ":") 4)})
                     res (if-not (:user/id res)
                           (conj res {:user/id (js/crypto.randomUUID)})
                           res)]
                 (println res)
                 (f res))))))

(defn add-user [{:keys [store setStore] :as ctx} address & extra]
  (t/add! ctx {:user/id (count (:user/id store))
               :user/ethereum-address address} (first extra)))

(defc User [this {:user/keys [id ethereum-address]}]
  #jsx [:div {:class "flex items-center"}
        [b/icon-button {:icon #jsx [:img {:class "rounded-md h-10 w-10"
                                          :data-dropdown-toggle "header-user-dropdown"
                                          :draggable false
                                          :onDragStart nil
                                          :src (blo (ethereum-address))}
                                    #_(Show show-name
                                            [:div {:class "px-2"}]
                                            (if-not (= first-name "")
                                              (str first-name " " last-name)
                                              (subs (str ethereum-address) 0 8)))]}]]
  #_[tt/tooltip {:id "header-user-tt"
                 :content (ethereum-address)}])

(def ui-user (comp/comp-factory User AppContext))
