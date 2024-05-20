(ns components.profile
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../composedb/client.mjs" :as cli]
            ["../normad.mjs" :as n]
            ["./blueprint/input.jsx" :as in]
            ["./blueprint/button.jsx" :as b]
            ["../ua.jsx" :as ua]
            ["../comp.jsx" :as comp]
            ["../Context.mjs" :refer [AppContext]]))

(def query-from-acc "query {
  viewer {
        basicProfile {
              id
              name
            }
  }
}
")

#_(defn on-click-mutation [app ident]
  (fn [e]
    (.preventDefault e)
    (-> (.executeQuery ^js (:compose @cli/client)
                       (basic-profile-mutation {:document (u/remove-ns-from-keys (nth (basicProfile-comp {} {}) 2))})
                       (mutation-vars (first (vals (n/pull store  [:basicProfile/id id])))))
        (.then (fn [response] (js/console.log response))))))

(defn set-field! [{:keys [store setStore] :as ctx} path value & convert-fn]
  (setStore (first path)
            (fn [x]
              (assoc-in x (rest path) value))))

(defn on-click [{:keys [store setStore] :as ctx}]
  (fn [e]
    (-> (.executeQuery (:compose @cli/client) query-from-acc)
        (.then (fn [response]
                 (js/console.log response)
                 (let [res (aget response "data" "viewer" "basicProfile")]
                   (println "got res:" (aget res "name"))
                   (n/add ctx {:basicProfile/id (aget res "id")
                               :basicProfile/name (aget res "name")})
                   (setStore :profile (fn [profile] [:basicProfile/id (aget res "id")]))))))))

(defn BasicProfile [ident]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        a (println ident)
        data (createMemo #(n/pull store (get-in store ident) [:basicProfile/id :basicProfile/name :basicProfile/username]))]
    #jsx [:div {} [:form {;:onSubmit on-click-mut
                          }
                   (in/input {:label "Name"
                              :placeholder "Your Name"
                              :on-change (fn [e] (set-field! ctx (conj ident :basicProfile/name) e.target.value))} (:name (data)))
                   (in/input {:label "Username"
                              :placeholder "Your Username"
                              :on-change (fn [e] (set-field! ctx (conj ident :basicProfile/username) e.target.value))} (:username (data)))]
          (b/button "Get Name" on-click)]))

(def ui-user (comp/comp-factory comp/User AppContext))
(def ui-user2 (comp/comp-factory ua/User AppContext))

(defn ProfilePage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(n/pull store (get store :profile) [:user/id]))
        u [:user/id 0]]
    #_(onMount ((on-click nil)))
    #jsx [:div {}
          (ui-user2 [:user/id (data)])
          #_(BasicProfile [:basicProfile/id (:basicProfile/id (data))])]))
