(ns pages.search
  (:require ["solid-js" :refer [For createSignal]]
            ["solid-js/web" :refer [Dynamic]]
            ["../comp.cljs" :as comp]
            ["../composedb/util.cljs" :as cu]
            ["./category/category.cljs" :as c]
            ["../transact.cljs" :as t]
            ["./blueprint/split.cljs" :as s]
            ["./blueprint/tabs.cljs" :as tabs]
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
  (let [[local setLocal] (createSignal {:split-items [{:selected 0
                                                       :tabs [{:title "Category"
                                                               :type :category
                                                               :props {:indent? false
                                                                       :open? true}
                                                               :comp (fn [props] #jsx [c/ui-category {:& props}])}]}]})]
    #jsx [:div {:class "flex flex-col w-full"}
          [s/Split {:& {:extra-class "mt-2"}}
           [For {:each (:split-items (local))}
            (fn [item i]
              #jsx [s/SplitItem {}
                    [tabs/Tabs {:& {:id "" :data-tabs-toggle "default-tab-content"
                                      :items #(:tabs item)}}]
                    [Dynamic {:component (:comp (nth (:tabs item) (:selected item)))
                              :ident (fn [] [:category/id (:category/id (category))])}

                     #_[tabs/TabContent {:& {:id "default-tab-content"
                                             :item (first (:tabs (local)))}}]]])]
           #_[s/SplitItem {}
              [tabs/Tabs {:& {:id "" :data-tabs-toggle "default-tab-content"
                              :items (fn [] (:tabs (local)))}}]
              [tabs/TabContent {:& {:id "default-tab-content"}}]

              [c/ui-category {:& {:ident (fn [] [:category/id (:category/id (category))])
                                  :indent? false
                                  :open? true}}]]]]))

(defn load-project [ctx ident]
  (cu/execute-eql-query ctx {ident ProjectReport.query}
                        (fn [r] (let [c (u/nsd (get-in r [:node :category]) :category)
                                      co (u/nsd (get-in r [:node :contract]) :contract)
                                      project (u/nsd (get-in r [:node]) :project)
                                      project (assoc (assoc project :project/category c) :project/contract co)]
                                  (t/add! ctx project)))))

(def ui-project-report (comp/comp-factory ProjectReport AppContext))

