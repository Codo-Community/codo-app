(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["@solidjs/router" :refer [useSearchParams]]
            ["../components/project_list.jsx" :as p]
            ["../composedb/client.mjs" :as cli]
            ["../utils.mjs" :as utils]
            ["../transact.mjs" :as t]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def query "query {
  projectIndex(last: 10) {
    edges {
      node {
        id
        name
        contract {
          id
          chain
          address
        }
      }
    }
  }
}
")

(defn SearchPage []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        params (useSearchParams)
        ident [:component/id :project-list]]
    (onMount (fn []
               (if (empty? (get-in store [:component/id :project-list :projects]))
                 (-> (.executeQuery (:compose @cli/client) query)
                     (.then (fn [response]
                              (let [res (-> response :data :projectIndex :edges)]
                                (doall (for [v res]
                                         (let [v (:node v)
                                               val (assoc  (dissoc (utils/nsd v :project) :contract) :project/contract (utils/nsd (:contract v) :contract))]
                                           (t/add! ctx val
                                                   {:append [:component/id :project-list :projects]})))))))))))
    #jsx [p/ui-project-list ident]))
