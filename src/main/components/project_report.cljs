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

(defc ProjectReport [this {:project/keys [id name start description {category [:id :name]} {contract [:id :chain]}]}]
  (let [ident (fn [category] [:category/id (:id (category))])]
    #jsx [:div {:class "flex flex-row w-screen"}
          #_(str [:category/id (category)])
          #_[:aside {:id "sidebar"
                     :class "w-16 h-full"
                     :aria-label "Sidebar"}
             [:div {:class "h-full px-3 py-4 overflow-hidden bg-gray-50 dark:bg-black"}
              [:ul {:class "space-y-2 font-medium"}
               [:li
                [:a {:href "#"
                     :class "flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group i-tabler-search"}]]]]]
          [c/ui-category (category)]
          (println (category))

          #_(str "n " (name) (id) (description) (contract) (category))]))

(def ui-project-report (comp/comp-factory ProjectReport AppContext))

(defn load-project [ctx ident]
  (cu/execute-eql-query ctx {ident (aget (ProjectReport.) "query")} :project
                        (fn [r] (let [c (u/nsd (get-in r [:node :category]) :category)
                                      co (u/nsd (get-in r [:node :contract]) :contract)]
                                  #_(println "d:" (u/nsd (get r :node) :project))
                                  #_(t/add! ctx (assoc (assoc (u/nsd (get r :node) :project) :project/category c) :project/contract co))
                                  (t/add! ctx (u/nsd (get r :node) :project))))))
