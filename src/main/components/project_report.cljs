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

(defc ProjectReport [this {:project/keys [id name start description {category [:category/id :category/name]} {contract [:contract/id :contract/chain]}]}]
  #jsx [:div {:class "flex flex-row w-screen"}
        #_[:aside {:id "sidebar"
                   :class "w-16 h-full"
                   :aria-label "Sidebar"}
           [:div {:class "h-full px-3 py-4 overflow-hidden bg-gray-50 dark:bg-black"}
            [:ul {:class "space-y-2 font-medium"}
             [:li
              [:a {:href "#"
                   :class "flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group i-tabler-search"}]]]]]
        #jsx [c/ui-category {:& {:ident [:category/id (:category/id (category))]}}]

        #_(str "n " (name) (id) (description) (contract) (category))])

(defn load-project [ctx ident]
  (cu/execute-eql-query ctx {ident ProjectReport.query}
                        (fn [r] (let [c (u/nsd (get-in r [:node :category]) :category)
                                         co (u/nsd (get-in r [:node :contract]) :contract)
                                         project (u/nsd (get-in r [:node]) :project)]
                                     (println "r: " r)
                                     (println "asx: d" (u/nsd (get r :node) :project))
                                     (t/add! ctx (assoc (assoc project :project/category c) :project/contract co))
                                     (t/add-ident! ctx [:category/id (:category/id c)] {:replace [:project/id (:project/id project) :project/category]})
                                     (t/add-ident! ctx [:contract/id (:contract/id co)] {:replace [:project/id (:project/id project) :project/contract]})
                                     #_(t/add! ctx (u/nsd (get r :node) :project))))))

(def ui-project-report (comp/comp-factory ProjectReport AppContext))

