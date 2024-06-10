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

(defn add-category-remote [ctx id]
  (cu/execute-gql-mutation ctx
                           category-mutation
                           {:i {:content (utils/drop-false {:name "cool"})}}
                           (fn [r] (println "add c: " r)
                             (let [category (utils/nsd (get-in r [:createCategory :document]) :category)]
                               (t/add! ctx category
                                       {:append [:category/id id :category/children]})
                               (cu/execute-gql-mutation ctx (create-link id (:category/id category)) {} (fn [r] (println r)))))))

(defn add-category-local [ctx id]
  (t/add! ctx {:category/id (js/crypto.randomUUID) :category/name "blah"}
          {:append [:category/id id :category/children]}))

#_(defm add-category [this id]
  (add [this] )
  (cdb [this] )
  )

(declare ui-category)
(declare Category)

(defc Category [this {:category/keys [id name children]}]
  #jsx [:div {:class "ml-2"}
        [b/button {:title (str "id: " (id))
                   :on-click #(if (utils/uuid? (id))
                                (add-category-remote ctx (id))
                                (add-category-local ctx (id)))}]
        [in/input {:label "Title"
                   :placeholder "Name ..."
                   :value name
                   :on-change #(comp/set! this :category/name %)}]
        [:div {:class "flex flex-col"}
         [Index {:each (children)}
          (fn [entity i]
            (ui-category {:ident entity}))]]])

(def ui-category (comp/comp-factory Category AppContext))
