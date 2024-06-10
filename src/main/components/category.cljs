(ns components.category
  (:require ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["../utils.cljs" :as utils]
            ["../composedb/util.cljs" :as cu]
            ["../transact.cljs" :as t]
            ["./blueprint/button.cljs" :as b]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn create-link [parentID childID]
  (str "mutation MyMutation {
  createCategoryLink(input: {content: {childID: " childID ", parentID: " parentID "}}) {
    document {
      childID
      parentID
    }
  }
}"))

(def category-mutation
  (str "mutation createCategory($i: CreateCategoryInput!){
          createCategory(input: $i){
            document { id name }
 }}"))

(defn add-category [ctx id]
  (cu/execute-gql-mutation ctx
                           category-mutation
                           {:i {:content (utils/drop-false {:name "cool"})}}
                           (fn [r] (println "add c: " r)
                             (t/add! ctx (utils/nsd (get-in r [:createCategory :document]) :category) {:append [:category/id id :category/children]}))))

(declare ui-category)

(defc Category [this {:contract/keys [id name children]}]
  #jsx [:div {}

        [b/button {:title (str "N: " (name))
                   :on-click #(add-category ctx (id))}]
        (str "ch " (children))])

(def ui-category (comp/comp-factory Category AppContext))
