(ns components.userprofile
  (:require ["../comp.mjs" :as comp]
            ["./blueprint/input.jsx" :as in]
            ["../utils.mjs" :as utils]
            ["../transact.mjs" :as t]
            ["./blueprint/button.jsx" :as b]
            ["../composedb/client.mjs" :as cli]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def query-from-acc "query {
  viewer {
    user {
      author { id }
      firstName
      lastName
    }
  }
}
")

#_{:viewer {:user {:author [:id]} :firstName :lastName}}

(defn basic-profile-mutation [query]
  (str "mutation setUser($i: SetUserInput!){
          setUser(input: $i){
            document { firstName lastName }
 } }"))

#_(eql-gql/query->graphql query)

(defn mutation-vars [{:user/keys [id firstName lastName] :as data}]
  {:i {:content (utils/drop-false {:firstName firstName :lastName lastName})}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} ident]
  (fn [e]
    (if e (.preventDefault e))
    (-> (.executeQuery (:compose @cli/client)
                       (basic-profile-mutation {:document [:firstName :lastName]})
                       (mutation-vars (n/pull store ident [:user/id :user/firstName :user/lastName])))
        (.then (fn [response] (js/console.log response))))))

(defn on-click [{:keys [store setStore] :as ctx}]
  (fn [e]
    (if e (.preventDefault e))
    (-> (.executeQuery (:compose @cli/client) query-from-acc)
        (.then (fn [response]
                 #_(js/console.log "response: " response)
                 (let [res (aget response "data" "viewer" "user")]
                   #_(println "got res:" (aget res "firstName"))
                   (t/add! ctx {:user/id (aget res "author" "id") #_(aget res "id")
                                :user/firstName (aget res "firstName")
                                :user/lastName (aget res "lastName")}
                           {:replace [:pages/id :profile :user]})
                   #_(setStore :profile (fn [profile] [:user/id (aget res "id")]))))))))


(defc UserProfile [this {:user/keys [id firstName lastName]}]
  #jsx [:form {:onSubmit (on-click-mutation ctx ident)}
        [:div {} (firstName)]
        [in/input {:label "First Name"
                   :placeholder "Your Name"
                   :on-change (fn [e] (t/set-field! ctx (conj ident :user/firstName) e.target.value))
                   } (firstName)]
        #_(in/input {:label "Last Name"
                     :placeholder "Your Last Name"
                     :on-change (fn [e] (t/set-field! ctx (conj ident :user/lastName) e.target.value))} (lastName))
        [:button {:onClick (on-click ctx)} "get"]
        #_(b/button "Get Name" (on-click ctx))])

(def ui-user-profile (comp/comp-factory UserProfile AppContext))
