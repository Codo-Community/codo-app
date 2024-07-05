(ns pages.search
  (:require ["solid-js" :refer [For createSignal onMount createMemo useContext]]
            ["./editor_context.cljs" :refer [EditorContext]]
            ["solid-js/web" :refer [Dynamic]]
            ["flowbite" :refer [initModals]]
            ["../comp.cljs" :as comp]
            ["../composedb/util.cljs" :as cu]
            ["./category/modal.cljs" :as cm]
            ["./project/proposal_modal.cljs" :as pm]
            ["./category/view.cljs" :as cv]
            ["../transact.cljs" :as t]
            ["./blueprint/split.cljs" :as s]
            ["./blueprint/tabs.cljs" :as tabs]
            ["../utils.cljs" :as u]
            ["./blueprint/modal.cljs" :as modal]
            ["./blueprint/input.cljs" :as in]
            ["./blueprint/dropdown.cljs" :as dr]
            ["./project_item.cljs" :as pi]
            ["../Context.cljs" :refer [AppContext]]
            [squint.string :as string]
            )
  (:require-macros [comp :refer [defc]]))

(defc ProjectReport [this {:project/keys [id name start description
                                          {category [:category/id :category/name {:category/creator [:category/isViewer]}
                                                     {:category/children [:category/id]}]}
                                          {contract [:contract/id :contract/chain]}] :or {:id (u/uuid)
                                                                                          :name "Proj"
                                                                                          :start "2021-01-01"
                                                                                          :description "Desc"
                                                                                          :category {:category/id (u/uuid)
                                                                                                     :category/name "Category"}}}]
  (let [comp-2-modal {:category (fn [props] #jsx [cm/ui-category-modal {:& props}])
                      :proposal (fn [props] #jsx [pm/ui-proposal-modal {:& props}])}
        [local setLocal] (createSignal {:modal {:comp nil
                                                :visible? false
                                                :ident nil}
                                        :split-items [{:selected 0
                                                       :tabs [{:title "Categories"
                                                               :type :category
                                                               :props {:indent? false
                                                                       :open? true}
                                                               :comp (fn [props] #jsx [cv/ui-category-view {:& (merge props {:indent? false
                                                                                                                             :open? true})}])}]}]})]
    (createMemo (fn [] (when (:modal (local)) (initModals))))
    #jsx [:div {:class "flex flex-col w-full items-center aasdsd"}
          [s/Split {:& {:extra-class "mt-2"}}
           [For {:each (:split-items (local))}
            (fn [item i]
              #jsx [s/SplitItem {}
                    [tabs/Tabs {:& {:id "" :data-tabs-toggle "default-tab-content"
                                    :items #(:tabs item)}}]
                    [Dynamic {:& {:component (:comp (nth (:tabs item) (:selected item)))
                                  :projectLocal local
                                  :setProjectLocal setLocal
                                  :ident (fn [] [:category/id (:category/id (category))])}}]])]]
          [modal/modal {:& {:id "planner-modal"
                            :body #jsx [Dynamic {:& (merge {:component (get comp-2-modal (-> (local) :modal :comp))
                                                            :ident (fn [] (-> (local) :modal :ident))} (-> (local) :modal :props))}]}}]]))

(defn load-project [ctx ident]
  (cu/execute-eql-query ctx {ident ProjectReport.query}
                        #_(fn [r] (let [c (u/nsd (get-in r [:node :category]) :category)
                                      co (u/nsd (get-in r [:node :contract]) :contract)
                                      project (u/nsd (get-in r [:node]) :project)
                                      project (assoc (assoc project :project/category c) :project/contract co)]
                                  (t/add! ctx project)))))

(def ui-project-report (comp/comp-factory ProjectReport AppContext))

