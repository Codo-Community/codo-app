(ns components.userprofile
  (:require ["../comp.mjs" :as comp]
            ["./blueprint/input.jsx" :as in]
            ["./blueprint/textarea.jsx" :as ta]
            ["../utils.mjs" :as utils]
            ["../transact.mjs" :as t]
            ["./blueprint/button.jsx" :as b]
            ["../composedb/client.mjs" :as cli]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def query-from-acc "query {
  viewer {
    id
    user {
      id
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

(defn on-click-mutation [{:keys [store setStore] :as ctx} {:user/keys [id firstName lastName] :as data}]
  (fn [e]
    (if e (.preventDefault e))
    (-> (.executeQuery (:compose @cli/client)
                       (basic-profile-mutation {:document [:firstName :lastName]})
                       (mutation-vars (data)))
        (.then (fn [response] (js/console.log response))))))

(defn on-click [{:keys [store setStore] :as ctx}]
  (fn [e]
    (if e (.preventDefault e))
    (-> (.executeQuery (:compose @cli/client) query-from-acc)
        (.then (fn [response]
                 (js/console.log "response: " response)
                 (let [res (-> response :data :viewer :user)]
                   (println (utils/nsd res :user))
                   (t/add! ctx (utils/nsd res :user)
                           {:replace [:pages/id :profile :user]})))))))

(defc UserProfile [this {:user/keys [id firstName lastName introduction] :as data}]
  #jsx [:form {:class "flex flex-col min-w-96 gap-3"
               :onSubmit (on-click-mutation ctx data)}
        [:span {:class "flex w-full gap-3"}
         [in/input {:label "First Name"
                    :placeholder "Your Name"
                    :value firstName
                    :on-change (fn [e] (t/set-field! ctx (conj children.children :user/firstName) e.target.value))}]
         [in/input {:label "Last Name"
                    :placeholder "Your Last Name"
                    :value lastName
                    :on-change (fn [e] (t/set-field! ctx (conj children.children :user/lastName) e.target.value))}]]
        [ta/textarea {:title "Description"
                      :placeholder "Introduce yourself ..."
                      :value introduction}]
        [:span {:class "flex w-full gap-3"}
         [b/button {:title "Submit"}]
         [b/button {:title "Get Name"
                    :on-click (on-click ctx)}]]])

(def ui-user-profile (comp/comp-factory UserProfile AppContext))
