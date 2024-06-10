(ns components.userprofile
  (:require ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["./blueprint/textarea.cljs" :as ta]
            ["../utils.cljs" :as utils]
            ["../transact.cljs" :as t]
            ["./blueprint/button.cljs" :as b]
            ["../geql.cljs" :as geql]
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
      lastName
      introduction
    }
  }
}
")

#_{:viewer {:user {:author [:id]} :firstName :lastName}}

(defn basic-profile-mutation [query]
  (str "mutation setUser($i: SetUserInput!){
          setUser(input: $i){
            document { firstName lastName introduction }
 } }"))

#_(eql-gql/query->graphql query)

(defn mutation-vars [{:user/keys [id firstName lastName introduction] :as data}]
  {:i {:content (utils/drop-false {:firstName firstName :lastName lastName :introduction introduction})}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} {:user/keys [id firstName lastName introduction] :as data}]
  (fn [e]
    (if e (.preventDefault e))
    (-> (.executeQuery (:compose @cli/client)
                       (basic-profile-mutation {:document [:id :firstName :lastName :introduction]})
                       (mutation-vars (data)))
        (.then (fn [response] (js/console.log response))))))

(defc UserProfile [this {:user/keys [id firstName lastName introduction] :as data}]
  #jsx [:form {:class "flex flex-col min-w-96 gap-3"
               :onSubmit (on-click-mutation ctx data)}
        [:span {:class "flex w-full gap-3"}
         [in/input {:label "First Name"
                    :placeholder "Your Name"
                    :value firstName
                    :on-change #(comp/set! this :user/firstName %)}]
         [in/input {:label "Last Name"
                    :placeholder "Your Last Name"
                    :value lastName
                    :on-change #(comp/set! this :user/lastName %)}]]
        [ta/textarea {:title "Introduction"
                      :placeholder "Introduce yourself ..."
                      :value introduction
                      :on-change #(comp/set! this :user/introduction %)}]
        [:span {:class "flex w-full gap-3"}
         [b/button {:title "Submit"}]]])

(defn ^:async load-user-profile [{:keys [store setStore] :as ctx} ident]
  (let [query UserProfile.query
        query (mapv #(second (string/split % "/")) query)
        query (geql/eql->graphql {ident query})]
    (-> (.executeQuery (:compose @cli/client) query)
        (.then (fn [response]
                 (let [res (-> response :data :node)]
                   (t/add! ctx (utils/nsd res :user))))))))


(def ui-user-profile (comp/comp-factory UserProfile AppContext))
