(ns components.category
  (:require ["solid-js" :refer [Show createSignal onMount]]
            ["../comp.cljs" :as comp]
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
                               (cu/execute-gql-mutation ctx (create-link id (:category/id category)) {} (fn [r] (println "re: " r)))))))

(declare ui-category)
(declare Category)

(defc Category [this {:category/keys [id name children] :or {id (u/uuid) name "Category" children []}
                      :local {editing? false open? false hovering? false}}]
  #jsx [:div {:class "flex flex-col ml-3 gap-1"}
        [:span {:class "flex flex-inline gap-2"
                  :onMouseEnter #(setLocal (assoc (local) :hovering? true))
                  :onMouseLeave #(setLocal (assoc (local) :hovering? false))}
           [:div {:class "flex gap-1 items-center"}
            [:button {:class (if (:open? (local)) "i-tabler-chevron-down" "i-tabler-chevron-right")
                      :onClick #(setLocal (assoc (local) :open? (not (:open? (local)))))}]
            [Show {:when (not (:editing? (local))) :fallback #jsx [in/input {:placeholder "Name ..."
                                                                             :value name
                                                                             :on-change (fn [e]
                                                                                          (println "p " (:ident props))
                                                                                          (setLocal (assoc (local) :editing? false))
                                                                                          (comp/set! ctx (:ident props) :category/name e))}]}
             [:span {:class "flex flex-inline gap-2 rounded-md dark:bg-zinc-800 p-2 mouse-pointer hover:ring-2"
                     :onClick #(setLocal (assoc (local) :editing? true))}
              [:h2 {:class "text-bold"} (name)]]]]
           [Show {:when (and (:hovering? (local)) (not (:editing? (local))))}
            [:span {:class "flex gap-2 items-center"}
             [:button {:class "i-tabler-plus"
                       :onClick #(comp/mutate! this {:add :new
                                                     :append [:category/id (id) :category/children]
                                                     :cdb true})}]
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
