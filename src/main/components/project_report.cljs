(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["../comp.cljs" :as comp]
            ["../composedb/util.cljs" :as cu]
            ["./category/category.cljs" :as c]
            ["../transact.cljs" :as t]
            ["./blueprint/split.cljs" :as s]
            ["../utils.cljs" :as u]
            ["./project_item.cljs" :as pi]
            ["../Context.cljs" :refer [AppContext]]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

(defc ProjectReport [this {:project/keys [id name start description
                                          {category [:category/id :category/name]}
                                          {contract [:contract/id :contract/chain]}] :or {:id (u/uuid)
                                                                                          :name "Proj"
                                                                                          :start "2021-01-01"
                                                                                          :description "Desc"
                                                                                          :category {:category/id (u/uuid)
                                                                                                     :category/name "Category"}}}]
  #jsx [s/Split {:& {:extra-class "mt-2"}}
        #_[:aside {:id "sidebar"
                   :class "w-16 h-full"
                   :aria-label "Sidebar"}
           [:div {:class "h-full px-3 py-4 overflow-hidden bg-gray-50 dark:bg-black"}
            [:ul {:class "space-y-2 font-medium"}
             [:li
              [:a {:href "#"
                   :class "flex items-center p-2 text-gray-900 rounded-lg dark:text-white hover:bg-gray-100 dark:hover:bg-gray-700 group i-tabler-search"}]]]]]
        [s/SplitItem {}
         [:span {:class "flex gap-3 items-center"} [:div {:class "i-tabler-list-tree"}]  [:h1 {:class "font-bold text-xl"} "Categories"]]
         [c/ui-category {:& {:ident (fn [] [:category/id (:category/id (category))])
                             :open? true}}]]
        [s/SplitItem {}
         [:span {:class "flex gap-3 items-center"} [:div {:class "i-tabler-notes"}]  [:h1 {:class "font-bold text-xl"} "Proposals"]]
         [c/ui-category {:& {:ident (fn [] [:category/id (:category/id (category))])
                             :indent? false
                             :open? true}}]]])

(defn load-project [ctx ident]
  (cu/execute-eql-query ctx {ident ProjectReport.query}
                        (fn [r] (let [c (u/nsd (get-in r [:node :category]) :category)
                                      co (u/nsd (get-in r [:node :contract]) :contract)
                                      project (u/nsd (get-in r [:node]) :project)
                                      project (assoc (assoc project :project/category c) :project/contract co)]

                                     (println "r: " r)
                                     (println "asx: d" (u/nsd (get r :node) :project))
                                     (t/add! ctx project)))))

(def ui-project-report (comp/comp-factory ProjectReport AppContext))

