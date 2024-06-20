(ns components.category
  (:require ["solid-js" :refer [Show createSignal onMount useContext]]
            ["@solid-primitives/active-element" :refer [createFocusSignal]]
            ["../../comp.cljs" :as comp]
            ["../blueprint/input.cljs" :as in]
            ["../../utils.cljs" :as utils]
            ["../../composedb/util.cljs" :as cu]
            ["../../utils.cljs" :as u]
            ["flowbite" :as fb]
            ["./query.cljs" :as cq]
            ["./menu.cljs" :as cm]
            ["@solidjs/router" :refer [cache createAsync]]
            ["../../transact.cljs" :as t]
            ["../blueprint/button.cljs" :as b]
            ["../../composedb/client.cljs" :as cli]
            ["../../normad.cljs" :as normad]
            ["../../Context.cljs" :refer [AppContext]])
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


(defn add-category-remote [ctx {:category/keys [id name color children] :as data} parent-id]
  (let [vars (utils/remove-ns data)
        vars (dissoc vars :id)
        vars {:i {:content (utils/drop-false vars)}}
        vars (if-not (u/uuid? id)
                 (assoc-in  vars [:i :id] id) vars)
        vars (utils/drop-false vars)
        mutation (if (u/uuid? id)
                   {:name "createCategory"
                    :fn create-mutation}
                   {:name "updateCategory"
                    :fn update-mutation})]
    (println vars)
    (cu/execute-gql-mutation ctx
                             (:fn mutation)
                             vars
                             (fn [r]
                               (let [category (utils/nsd (get-in r [(:name mutation) :document]) :category)
                                     stream-id (:category/id category)]
                                 ; TODO: fix changing uuid with stream-id here
                                 ; so the data will be like {:category/id #uuid, :category/name "xyz" ...}
                                 ; we need to do a function which deep recursively updates the normalized state whenever [:category/id #uuid] shows up as a reference
                                 ; and inside the :category/id table {:category/id {:#uuid-xyz {:category/id :#uuid-xyz ... -> :category/id stream-id
                                 #_(normad/swap-uuids! ctx id stream-id)

                                 (when (u/uuid? id)
                                   (cu/execute-gql-mutation ctx create-link-mut
                                                            {:i {:content {:parentID parent-id :childID (:category/id category)}}}
                                                            (fn [r] #_(println "re: " r)))))))))

(declare ui-category)
(declare Category)

(defn load-category [ctx id]
  (cu/execute-gql-mutation ctx (cq/simple id) {} (fn [r]
                                                   (let [new-children (mapv #(utils/nsd (->  % :node :child) :category) (-> r :node :children :edges))]
                                                     (t/add! ctx
                                                             (assoc (utils/nsd (-> r :node) :category)
                                                                    :category/children new-children))))))

(def load-category-c (cache (fn [id]
                              (let [ctx (useContext AppContext)]
                                (println "loading category" id)
                                (load-category ctx id)))))

(defn load-category-cache [id]
  (load-category-c id))

(defc Category [this {:category/keys [id name color children] :or {id (u/uuid) name "Category" children nil color :gray}
                                        ;:props [parent]
                      :local {editing? false open? false hovering? false selected nil}}]
  (do
    (onMount (fn [] (when (:open? props)
                      (println "loading category" (id))
                      (load-category ctx (id))
                      (setLocal (assoc (local) :open? (:open? props))))))
    #jsx [:div {:class "flex flex-col ml-3 gap-1"}
          [Show {:when (not (= (name) "Root"))}
           [:span {:class "flex flex-inline gap-2 mouse-pointer"
                   :onMouseEnter #(setLocal (assoc (local) :hovering? true))
                   :onMouseLeave #(setLocal (assoc (local) :hovering? false))}
            [:div {:class "flex gap-1 items-center"}
             [:button {:class (if (:open? (local)) "i-tabler-chevron-down" "i-tabler-chevron-right")
                       :onClick (fn [e]
                                  (when-not (:open? (local))
                                    (load-category ctx (id)))
                                  (setLocal (assoc (local) :open? (not (:open? (local))))))}]
             [Show {:when (not (:editing? (local))) :fallback #jsx [in/input {:placeholder "Name ..."
                                                                              :value name
                                                                              :on-focus-out (fn [e] (setLocal (assoc (local) :editing? false)))
                                                                              :on-change (fn [e]
                                                                                           (setLocal (assoc (local) :editing? false))
                                                                                           (comp/set! this (:ident props) :category/name e)
                                        ; TODO: need to auto swap uuids for streamIDs
                                                                                           (add-category-remote ctx (data) (:parent props)))}]}
              [:div {:class (str "flex flex-inline gap-2 rounded-md p-2 mouse-pointer focus:ring-2 " (condp = (color)
                                                                                                       :green "bg-green-800"
                                                                                                       :blue "bg-blue-800"
                                                                                                       :red "bg-red-800"
                                                                                                       :yellow "bg-yellow-800"
                                                                                                       :gray "bg-zinc-800"
                                                                                                       "bg-zinc-800"))
                     :tabindex 0
                     :onClick #(setLocal (assoc (local) :editing? true))}
               [:h2 {:class "text-bold"} (name)]]]]
            [Show {:when (and (:hovering? (local)) (not (:editing? (local))))}
             [cm/ui-category-menu {:&  (conj props {:this this} (data))}]]]]
          [Show {:when (:open? (local))}
           [:div {:class "flex flex-col gap-1"}
            [For {:each (children)}
             (fn [entity i]
               (ui-category {:ident entity
                             :parent (id)}))]]]]))

(def ui-category (comp/comp-factory Category AppContext))
