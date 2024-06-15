(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["../comp.cljs" :as comp]
            ["../composedb/util.cljs" :as cu]
            ["./category.cljs" :as c]
            ["../transact.cljs" :as t]
            ["../utils.cljs" :as u]
            ["./project_item.cljs" :as pi]
            ["../Context.cljs" :refer [AppContext]]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

#_(defn query [id] (str "query {
  node(id: " id " ) {
    ... on Project {
      id
      name
      start
      description
      contract {
        chain
      }
    }
  }
}
"))

(def load-query [:project/id :project/name :project/start :project/description {:project/category [:id :name]} {:project/contract [:id :chain]}])

(defn load-project [ctx ident]
  (cu/execute-eql-query ctx {ident load-query} :project
                        (fn [r] (let [c (u/nsd (get-in r [:node :category]) :category)
                                      co (u/nsd (get-in r [:node :contract]) :contract)
                                      project (u/nsd (get-in r [:node]) :project)]
                                  (println "asx: r " r)
                                  (println "asx: d" (u/nsd (get r :node) :project))
                                  (t/add! ctx (assoc (assoc project :project/category c) :project/contract co))
                                  (t/add-ident! ctx [:category/id (:category/id c)] {:replace [:project/id (:project/id project) :project/category]})
                                  (t/add-ident! ctx [:contract/id (:contract/id co)] {:replace [:project/id (:project/id project) :project/contract]})
                                  #_(t/add! ctx (u/nsd (get r :node) :project))))))

(defc ProjectReport [this {:project/keys [id name start description category contract]}]
  (do (onMount (fn [] (println "response: load") (load-project ctx this.ident)))
      #jsx [:div {:class "flex flex-row w-screen"}
            #_[:aside {:id "sidebar"
                       :class "w-16 h-full"
                       :aria-label "Sidebar"}
               [:div {:class "h-full px-3 py-4 overflow-hidden bg-gray-50 dark:bg-black"}
                [:ul {:class "space-y-2 font-medium"}
                 [:li
                  [:a {:href "#"
                       :class "flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group i-tabler-search"}]]]]]
            [c/ui-category {:& {:ident category}}]

            #_(str "n " (name) (id) (description) (contract) (category))]))

(def ui-project-report (comp/comp-factory ProjectReport AppContext))

