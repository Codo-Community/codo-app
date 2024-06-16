(ns components.category
  (:require ["solid-js" :refer [Show createSignal onMount]]
            ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["../utils.cljs" :as utils]
            ["../composedb/util.cljs" :as cu]
            ["../utils.cljs" :as u]
            ["./category_query.cljs" :as cq]
            ["../transact.cljs" :as t]
            ["./blueprint/button.cljs" :as b]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def create-link-mut
  "mutation createCategoryLink($i: CreateCategoryLinkInput!) {
   createCategoryLink(input: $i) {
    document {
      childID
      parentID
    }
  }
}")

(def update-link-mut
  "mutation createCategoryLink($i: CreateCategoryLinkInput!) {
   createCategoryLink(input: $i) {
    document {
      childID
      parentID
    }
  }
}")


(def create-mutation
  "mutation createCategory($i: CreateCategoryInput!){
     createCategory(input: $i){
       document { id name }
     }
}")

(def update-mutation
  "mutation updateCategory($i: UpdateCategoryInput!){
     updateCategory(input: $i){
       document { name }
     }
}")


(defn add-category-remote [ctx data parent-id]
  (let [vars (utils/drop-false (utils/remove-ns data))
        vars (dissoc vars :id)
        vars {:i {:content vars}}
        vars (if-not (u/uuid? (:id vars)) (assoc-in vars [:i :id] (:category/id data)) vars)
        mutation (if (u/uuid? (:id vars)) {:name "createCategory"
                                           :fn create-mutation}  {:name "updateCategory"
                                                                  :fn update-mutation})]
    (println vars)
    (cu/execute-gql-mutation ctx
                             (:fn mutation)
                             vars
                             (fn [r] (println "add c: " r)
                               (let [category (utils/nsd (get-in r [(:name mutation) :document]) :category)]
                                 #_(t/add! ctx category
                                           {:append [:category/id id :category/children]})
                                 (if (u/uuid? (:id vars))
                                   (cu/execute-gql-mutation ctx create-link-mut
                                                            {:i {:content {:parentID parent-id :childID (:category/id category)}}}
                                                            (fn [r] #_(println "re: " r)))))))))

(declare ui-category)
(declare Category)

(defn load-category [ctx id]
  (cu/execute-gql-mutation ctx (cq/simple id) {} (fn [r] (println "a")
                                                   (let [new-children (mapv #(utils/nsd (->  % :node :child) :category) (-> r :node :children :edges))]
                                                     (t/add! ctx
                                                             (assoc (utils/nsd (-> r :node) :category)
                                                                    :category/children new-children))))))

(defc Category [this {:category/keys [id name children] :as data :or {id (u/uuid) name "Category" children nil}
                      :props [parent]
                      :local {editing? false open? false hovering? false}}]
  #jsx [:div {:class "flex flex-col ml-3 gap-1"}
        [:span {:class "flex flex-inline gap-2"
                :onMouseEnter #(setLocal (assoc (local) :hovering? true))
                :onMouseLeave #(setLocal (assoc (local) :hovering? false))}
         [:div {:class "flex gap-1 items-center"}
          [:button {:class (if (:open? (local)) "i-tabler-chevron-down" "i-tabler-chevron-right")
                    :onClick (fn [e]
                               (load-category ctx (id))
                               (setLocal (assoc (local) :open? (not (:open? (local))))))}]
          [Show {:when (not (:editing? (local))) :fallback #jsx [in/input {:placeholder "Name ..."
                                                                           :value name
                                                                           :on-focus-out (fn [e] (setLocal (assoc (local) :editing? false)))
                                                                           :on-change (fn [e]
                                                                                        (setLocal (assoc (local) :editing? false))
                                                                                        (comp/set! ctx (:ident props) :category/name e)
                                                                                        ; TODO: need to auto swap uuids for streamIDs
                                                                                        (add-category-remote ctx (data) (:parent props)))}]}
           [:span {:class "flex flex-inline gap-2 rounded-md dark:bg-zinc-800 p-2 mouse-pointer hover:ring-2"
                   :onClick #(setLocal (assoc (local) :editing? true))}
            [:h2 {:class "text-bold"} (name)]]]]
         [Show {:when (and (:hovering? (local)) (not (:editing? (local))))}
          [:span {:class "flex gap-2 items-center"}
           [:button {:class "i-tabler-plus"
                     :onClick #(comp/mutate! this {:add :new
                                                   :append [:category/id (id) :category/children]})}]
           [:button {:class "i-tabler-palette"}]
           [:button {:class "i-tabler-icons"}]
           [:button {:class "i-tabler-x"
                     :onClick #(comp/mutate! this {:remove [:category/id (id)]
                                                   :from [:category/id (:parent props) :category/children]
                                                   :cdb true})}]]]]
        [Show {:when (:open? (local))}
         [:div {:class "flex flex-col gap-1"}
          [For {:each (children)}
           (fn [entity i]
             (ui-category {:ident entity
                           :parent (id)}))]]]])

(def ui-category (comp/comp-factory Category AppContext))
