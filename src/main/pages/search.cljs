(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["@solidjs/router" :refer [useSearchParams cache]]
            ["../components/project_list.cljs" :as p]
            ["../composedb/client.cljs" :as cli]
            ["../utils.cljs" :as utils]
            ["../transact.cljs" :as t]
            ["../comp.cljs" :as comp]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def project-fragment
 "fragment ProjectFragment on Node {
  ...on Project {
        id
        name
        description
        contract {
          id
          chain
          address
        }
      }
}\n\n")

(def my-projects ["query {
  viewer {
    projectList(last: 100) {
      edges {
        node {
            ...ProjectFragment
        }
      }
    }
  }
}" [:data :viewer :projectList :edges]])

(def list-query ["query {
  projectIndex(last: 10) {
    edges {
      node {
        ...ProjectFragment
        }
      }
    }
  }
}
" [:data :projectIndex :edges]])

(def load-projects (cache (fn [query] (let [ctx (useContext AppContext)]
                                        (-> (.executeQuery (:compose @cli/client) (str project-fragment (first query)))
                                            (.then (fn [response]
                                                     (let [res (get-in response (second query))]
                                                       (println "response: " response)
                                                       (doall (for [v res]
                                                                (let [v (:node v)
                                                                      val (assoc  (dissoc (utils/nsd v :project) :contract) :project/contract (utils/nsd (:contract v) :contract))]
                                                                  (t/add! ctx val {:append [:component/id :project-list :projects]}))))))))))))

(defc SearchPage [this {:keys [component/id projects]}]
  #jsx [:div {}
        [p/ui-project-list {:& {:ident [:component/id :project-list]}}]])

(def ui-search-page (comp/comp-factory SearchPage AppContext))
