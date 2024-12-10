(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["@solidjs/router" :refer [useSearchParams cache]]
            ["../components/project_list.cljs" :as p]
            ["../composedb/client.cljs" :as cli]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [sqeave :refer [defc]]))

(def project-fragment "fragment ProjectFragment on Node {
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
}")

(def my-projects ["
  query {
  viewer {
    projectList(last: 100) {
      edges {
        node {
            id
        name
        description
        contract {
          id
          chain
          address
        }
        }
      }
    }
  }
}" [:data :viewer :projectList :edges]])

(def list-query ["query {
  projectIndex(last: 10) {
    edges {
      node {
        id
        name
        description
        contract {
          id
          chain
          address
        }
        }
      }
    }
  }
" [:data :projectIndex :edges]])

(def load-projects (cache (fn [query] (let [ctx (useContext AppContext)]
                                        (println "q: " (first query))
                                        (-> (cli/exec-query (first query))
                                            (.then (fn [response]
                                                     (let [res (get-in response (second query))]
                                                       (println "response: " response)
                                                       (doall (for [v res]
                                                                (let [v (:node v)
                                                                      val (assoc  (dissoc (sqeave/nsd v :project) :contract) :project/contract (sqeave/nsd (:contract v) :contract))]
                                                                  (sqeave/add! ctx [val] {:replace [:component/id :project-list :projects]}))))))))))))

(defc SearchPage [this {:keys [component/id projects]}]
  #jsx [p/ProjectList {:& {:ident [:component/id :project-list]}}])
