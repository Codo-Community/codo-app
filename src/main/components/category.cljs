(ns components.category
  (:require ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["../utils.cljs" :as utils]
            ["../composedb/util.cljs" :as cu]
            ["../utils.cljs" :as u]
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

(defn add-category-remote [ctx id]
  (cu/execute-gql-mutation ctx
                           category-mutation
                           {:i {:content (utils/drop-false {:name "cool"})}}
                           (fn [r] (println "add c: " r)
                             (let [category (utils/nsd (get-in r [:createCategory :document]) :category)]
                               (t/add! ctx category
                                       {:append [:category/id id :category/children]})
                               (cu/execute-gql-mutation ctx (create-link id (:category/id category)) {} (fn [r] (println r)))))))

(declare ui-category)
(declare Category)

(defc Category [this {:category/keys [id name children] :or {id (u/uuid) name "Category" children []}}]
  #jsx [:div {:class "ml-2"}
        [:span {:class "flex"}
         [b/button {:title (str "id: " (id))
                    :on-click #(comp/mutate! this {:add :new
                                                   :append [:category/id (id) :category/children]
                                                   :cdb true})}]
         [b/button {:title "Remove"
                    :on-click #(comp/mutate! this {:remove :this
                                                   :cdb true})}]]
        [in/input {:label "Title"
                   :placeholder "Name ..."
                   :value name
                   :on-change #(comp/set! ctx ((:ident props)) :category/name %)}]
        [:div {:class "flex flex-col"}
         [Index {:each (children)}
          (fn [entity i]
            (ui-category {:ident entity}))]]])

(def ui-category (comp/comp-factory Category AppContext))

#_(println "cc " (ui-category {:ident [:category/id "1"]}))
