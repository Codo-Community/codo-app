(ns components.user
  (:require ["blo" :refer [blo]]
            ["./blueprint/tooltip.cljs" :as tt]
            ["./blueprint/button.cljs" :as b]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../utils.cljs" :as utils]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]]
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
               (let [res (conj (utils/nsd (-> response :data :viewer :user) :user)
                               {:user/ethereum-address (nth (string/split (-> response :data :viewer :id) ":") 4)})
                     res (if-not (:user/id res)
                           (conj res {:user/id (js/crypto.randomUUID)})
                           res)]
                 (f res))))))

(defn add-user [{:keys [store setStore] :as ctx} address & extra]
  (t/add! ctx {:user/id (count (:user/id store))
               :user/ethereum-address address} (first extra)))

(defc User [this {:user/keys [id firstName ethereum-address]}]
  #jsx [:div {:class "flex items-center"}
        [b/icon-button {:icon #jsx [:img {:class "rounded-md h-10 w-10"
                                          :data-dropdown-toggle "header-user-dropdown"
                                          :draggable false
                                          :onDragStart nil
                                          :src (blo (or (ethereum-address) "0x0"))}
                                    #_(Show show-name
                                            [:div {:class "px-2"}]
                                            (if-not (= first-name "")
                                              (str first-name " " last-name)
                                              (subs (str ethereum-address) 0 8)))]}]]
  #_[tt/tooltip {:id "header-user-tt"
                 :content (ethereum-address)}])

(def ui-user (comp/comp-factory User AppContext))
