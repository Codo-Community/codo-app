(ns components.userprofile
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["./blueprint/input.cljs" :as in]
            ["./blueprint/textarea.cljs" :as ta]
            ["./blueprint/button.cljs" :as b]
            ["../geql.cljs" :as geql]
            ["../composedb/client.cljs" :as cli]
            ["../composedb/util.cljs" :as cu]
            [squint.string :as string])
  (:require-macros [sqeave :refer [defc]]))

(def query-from-acc "query {
    user {
      id
      firstName
      lastName
      introduction
      account { id }
    }
  }
")

(defn profile-q [id]
  (str "query {
  node(id: \""id"\") {
    ... on User {
     id
     firstName
     introduction
     lastName
     }
  }
}"))

(def basic-profile-mutation
  (str "mutation setUser($i: SetUserInput!){
          setUser(input: $i){
            document { firstName lastName introduction }
 } }"))

#_(eql-gql/query->graphql query)

(defn mutation-vars [{:user/keys [id firstName lastName introduction] :as data}]
  {:i {:id id
       :content {:firstName firstName :lastName lastName :introduction introduction}}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} {:user/keys [id firstName lastName introduction] :as data}]
  (fn [e]
    (if e (.preventDefault e))
    (println (data))
    (cu/execute-gql-mutation ctx basic-profile-mutation
                             (mutation-vars (data))
                             (fn [response] (println response)))))

(defc UserProfile [this {:user/keys [id firstName lastName introduction] :as data}]
  #jsx [:form {:class "flex flex-col gap-4 px-4"
               :onSubmit (on-click-mutation ctx this.data)}
        [:h1 {:class "text-xl"} "Profile"]
        [:span {:class "flex w-full gap-3"}
         [in/input {:label "First Name"
                    :placeholder "Your Name"
                    :value firstName
                    :on-change #(sqeave/set! this :user/firstName %)}]
         [in/input {:label "Last Name"
                    :placeholder "Your Last Name"
                    :value lastName
                    :on-change #(sqeave/set! this :user/lastName %)}]]
        [ta/textarea {:title "Introduction"
                      :placeholder "Introduce yourself ..."
                      :value introduction
                      :on-change #(sqeave/set! this :user/introduction %)}]
        [:span {:class "flex w-full gap-3"}
         [b/button {:title "Submit"}]]])

(defn ^:async load-user-profile [{:keys [store setStore] :as ctx} ident]
  (cu/execute-gql-query ctx (profile-q (second ident)) {}
                        (fn [profile] (println "profile: " profile) (sqeave/add! ctx profile)))

  #_(cu/execute-eql-query ctx {ident UserProfileClass.query}
                          (fn [profile] (println "profile: " profile) (sqeave/add! ctx profile)))

  #_(let [query UserProfileClass.query
          query (mapv #(second (string/split % "/")) query)
          query (geql/eql->graphql {ident query})
          a (println "sq: " query)]
      (-> (cli/exec-query query)
          (.then (fn [response]
                   (let [res (-> response :data :node)]
                     (sqeave/add! ctx (sqeave/nsd res :user))))))))
