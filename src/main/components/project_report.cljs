(ns pages.search
  (:require ["solid-js" :refer [For createSignal onMount createMemo useContext]]
            ["solid-js/web" :refer [Dynamic]]
            ["flowbite" :refer [initModals]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../composedb/util.cljs" :as cu]
            ["./category/modal.cljs" :as cm]
            ["./project/proposal_modal.cljs" :as pm]
            ["./category/view.cljs" :as cv]
            ["./blueprint/split.cljs" :as s]
            ["./blueprint/tabs.cljs" :as tabs]
            ["./blueprint/modal.cljs" :as modal])
  (:require-macros [sqeave :refer [defc]]))

(defc ProjectReport [this {:project/keys [id name start description
                                          {category [:category/id :category/name {:category/creator [:ceramic-account/id]}
                                                     {:category/children [:category-link/id]}]}
                                          {contract [:contract/id :contract/chain]}] :or {:id (sqeave/uuid)
                                                                                          :name "Proj"
                                                                                          :start "2021-01-01"
                                                                                          :description "Desc"
                                                                                          :category {:category/id (sqeave/uuid)
                                                                                                     :category/name "Category"}}}]
  (let [comp-2-modal {:category (fn [props] #jsx [cm/CategoryModal {:& props}])
                      :proposal (fn [props] #jsx [pm/ProposalModal {:& props}])}
        [local setLocal] (createSignal {:modal {:comp nil
                                                :visible? false
                                                :ident nil}
                                        :split-items [{:selected 0
                                                       :tabs [{:title "Categories"
                                                               :type :category
                                                               :props {:indent? false
                                                                       :open? true}
                                                               :comp (fn [props] #jsx [cv/CategoryView {:& (merge props {:indent? false
                                                                                                                         :open? true})}])}]}]})]
    (createMemo (fn [] (when (:modal (local)) (initModals))))
    #jsx [:div {:class "flex flex-col w-full items-center"}
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
  (cu/execute-eql-query ctx {ident ProjectReportClass.query}
                        (fn [project] (println "project: " project) (sqeave/add! ctx project {:replace [:component/id :header :active-project]
                                                                                         :check-session? false}))))
