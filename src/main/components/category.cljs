(ns components.category
  (:require ["../comp.mjs" :as comp]
            ["./blueprint/input.jsx" :as in]
            ["../utils.mjs" :as utils]
            ["../composedb/util.mjs" :as cu]
            ["../transact.mjs" :as t]
            ["./blueprint/button.jsx" :as b]
            ["../composedb/client.mjs" :as cli]
            ["../Context.mjs" :refer [AppContext]])
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
