(ns components.profile
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../composedb/client.mjs" :as cli]
            ["../normad.mjs" :as n]
            ["./blueprint/input.jsx" :as in]
            ["./blueprint/button.jsx" :as b]
            ["./user.jsx" :as u :refer [ui-user]]
            ["../comp.jsx" :as comp]
            ["../transact.mjs" :as t]
            ["../utils.mjs" :as utils]
            ["../Context.mjs" :refer [AppContext]]))

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

#_{:viewer {:user {:author [id]} :firstName :lastName}}

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
      (.preventDefault e)
      (-> (.executeQuery (:compose @cli/client)
                         (basic-profile-mutation {:document [:firstName :lastName]})
                         (mutation-vars (n/pull store ident [:user/id :user/firstName :user/lastName])))
          (.then (fn [response] (js/console.log response))))))

(defn on-click [{:keys [store setStore] :as ctx}]
  (fn [e]
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

(defn UserProfile [ident]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(n/pull store (get-in store ident) [:user/id :user/firstName :user/lastName]))]
    #jsx [:div {}
          [:form {:onSubmit (on-click-mutation ctx ident)}
           (in/input {:label "First Name"
                      :placeholder "Your Name"
                      :on-change (fn [e] (t/set-field! ctx (conj ident :user/firstName) e.target.value))} (:user/firstName (data)))
           (in/input {:label "Last Name"
                      :placeholder "Your Last Name"
                      :on-change (fn [e] (t/set-field! ctx (conj ident :user/lastName) e.target.value))} (:user/lastName (data)))]
          (b/button "Get Name" (on-click ctx))]))

(defn ProfilePage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(n/pull store [:pages/id :profile] [:user]))]
    #_(onMount ((on-click nil)))
    #jsx [:div {}
          #_(ui-user (data))
          (UserProfile (data))]))

#_(defc ProfilePage []
  )
