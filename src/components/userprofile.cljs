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
                 (js/console.log "response: " response)
                 (let [res (-> response :data :viewer :user)]
                   #_(println "got res:" (aget res "firstName"))
                   (t/add! ctx {:user/id (aget res "author" "id") #_(aget res "id")
                                  :user/firstName (aget res "firstName")
                                  :user/lastName (aget res "lastName")}
                             {:replace [:pages/id :profile :user]})
                   #_(setStore :pages/id (fn [t] (assoc-in t [:profile :user] [:user/id (-> res :author :id)])))))))))

(defc UserProfile [this {:user/keys [id firstName lastName]}]
  #jsx [:form {:onSubmit (on-click-mutation ctx ident)}
        [in/input {:label "First Name"
                   :placeholder "Your Name"
                   :value firstName
                   :on-change (fn [e] (t/set-field! ctx (conj ident.children :user/firstName) e.target.value))}]
        [in/input {:label "Last Name"
                   :placeholder "Your Last Name"
                   :value lastName
                   :on-change (fn [e] (t/set-field! ctx (conj ident.children :user/lastName) e.target.value))}]
        (b/button "Get Name" (on-click ctx))])

(def ui-user-profile (comp/comp-factory UserProfile AppContext))
