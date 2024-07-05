(ns components.category
  (:require ["solid-js" :refer [Show createSignal useContext onMount]]
            ["@solid-primitives/active-element" :refer [createFocusSignal]]
            ["../../comp.cljs" :as comp]
            ["../blueprint/input.cljs" :as in]
            ["../../utils.cljs" :as utils]
            ["../../composedb/util.cljs" :as cu]
            ["../../utils.cljs" :as u]
            ["../project/proposal.cljs" :as pr]
            ["./query.cljs" :as cq]
            ["./menu.cljs" :as cm]
            ["./context.cljs" :refer [FilterContext]]
            ["@solidjs/router" :refer [cache createAsync useParams]]
            ["../../transact.cljs" :as t]
            ["../blueprint/button.cljs" :as b]
            ["../../composedb/client.cljs" :as cli]
            ["../../normad.cljs" :as normad]
            ["flowbite" :refer [initModals]]
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
  "mutation updateCategoryLink($i: UpdateCategoryLinkInput!) {
   updateCategoryLink(input: $i) {
    document {
      childID
      parentID
    }
  }
}")


(def create-mutation
  "mutation createCategory($i: CreateCategoryInput!){
     createCategory(input: $i){
       document { id name color description }
     }
}")

(def update-mutation
  "mutation updateCategory($i: UpdateCategoryInput!){
     updateCategory(input: $i){
       document { id name color description }
     }
}")


(defn remove-category-remote [ctx id]
  (cu/execute-gql-mutation ctx update-mutation
                           {:i {:content {}
                                :options {:shouldIndex false}
                                :id id}} (fn [r])))

(defn add-category-remote [ctx {:category/keys [id name color description] :as data} parent-id]
  (let [vars (utils/remove-ns data)
        vars (dissoc vars :id)
        vars {:i {:content (utils/drop-false vars)}}
        vars (if-not (u/uuid? id)
               (assoc-in vars [:i :id] id)
               (assoc-in vars [:i :content :created] (.toLocaleDateString (js/Date.) "sv")))
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
                                     a (println "category: " category)
                                     stream-id (:category/id category)]
                                        ; TODO: fix changing uuid with stream-id here
                                        ; so the data will be like {:category/id #uuid, :category/name "xyz" ...}
                                        ; we need to do a function which deep recursively updates the normalized state whenever [:category/id #uuid] shows up as a reference
                                        ; and inside the :category/id table {:category/id {:#uuid-xyz {:category/id :#uuid-xyz ... -> :category/id stream-id
                                 #_(normad/swap-uuids! ctx id stream-id)
                                 (when (and (u/uuid? id) parent-id)
                                   (cu/execute-gql-mutation ctx create-link-mut
                                                            {:i {:content {:parentID parent-id :childID (:category/id category)}}}
                                                            #_(fn [r] (println "re: " r)))))))))

(declare ui-category)
(declare Category)

(defn load-category [ctx id]
  (cu/execute-gql-query ctx (cq/simple id) {} (fn [r]
                                                (let [ps (mapv #(:proposal/id %) (get r :category/proposals))]
                                                  (println "ps: " r)
                                                  (println "ps: " ps)
                                                  (t/add! ctx r)
                                                  (mapv #(cu/execute-gql-query ctx (pr/vote-count-query % :up) {}
                                                                               (fn [r] (t/add! ctx {:proposal/id % :proposal/count-up (:voteCount r)}))) ps)
                                                  (mapv #(cu/execute-gql-query ctx (pr/vote-count-query % :down) {}
                                                                               (fn [r] (t/add! ctx {:proposal/id % :proposal/count-down (:voteCount r)}))) ps)
                                                  ))))

(def load-category-c (cache (fn [id]
                              (let [ctx (useContext AppContext)]
                                (println "loading category" id)
                                (load-category ctx id)))))

(defn load-category-cache [id]
  (load-category-c id))

(defc Category [this {:category/keys [id name color {children [:category-link/id {:category-link/child [:category/id]}]}
                                      {creator [:id :isViewer]} proposals]
                      :or {id (u/uuid) name "Category" children nil color :gray proposals []}
                      :local {editing? false open? false hovering? false selected nil indent? true show-proposals? true}}]
  (let [filters (useContext FilterContext)]
    (onMount (fn [] (when (:open? props)
                      #_(println "loading category: " (id))
                      #_(load-category ctx (id))
                      (setLocal (assoc (local) :open? (:open? props)))
                      (initModals))))
    #jsx [:div {:class (str "flex flex-col gap-1 " (if (:indent? (local)) "ml-1" ""))}
          [:span {:class "flex flex-inline gap-2 mouse-pointer"
                  :onMouseEnter #(setLocal (assoc (local) :hovering? true))
                  :onMouseLeave #(setLocal (assoc (local) :hovering? false))}
           [:div {:class "flex gap-1 items-center"}
            [:button {:class (if (:open? (local)) "i-tabler-chevron-down" "i-tabler-chevron-right")
                      :onClick (fn [e]
                                 (when (and (not (:open? (local))) (not (u/uuid? (id))))
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

             [:button {:data-modal-target "planner-modal"
                       :data-modal-toggle "planner-modal"

                       :onClick #((:setProjectLocal props) (assoc ((:projectLocal props)) :modal {:comp :category
                                                                                                  :props {:parent (:parent props)}
                                                                                                  :ident [:category/id (id)]}))}  (name)]

             #_[:div {:class (str "flex flex-inline gap-2 rounded-md p-1 text-sm mouse-pointer focus:ring-2 " (condp = (color)
                                                                                                                :green "bg-green-800"
                                                                                                                :blue "bg-blue-800"
                                                                                                                :red "bg-red-800"
                                                                                                                :yellow "bg-yellow-800"
                                                                                                                :gray "bg-zinc-800"
                                                                                                                "bg-none"))
                      :tabindex 0
                      :onClick #(setLocal (assoc (local) :editing? true))}
                [:h2 {:class "text-bold"} (name)]]]]
           [Show {:when (and (:hovering? (local)) (not (:editing? (local))))}
            [cm/ui-category-menu {:&  {:ident (:ident props)
                                       :parent (:parent props)}}]]]
          [Show {:when (:open? (local))}
           [:div {:class "flex flex-col gap-1"}
            [Show {:when (:show-proposals? (filters))}
             [:div {:class "flex flex-col gap-1"}
              [For {:each (proposals)}
               (fn [proposal i]
                 #jsx [pr/ui-proposal {:& {:ident proposal
                                           :parent (id)
                                           :projectLocal (:projectLocal props)
                                           :setProjectLocal (:setProjectLocal props)}}])]]]
            [Show {:when (vector? (children))}
             [For {:each (mapv (fn [x] [:category/id (:category-link/child x)]) (children))}
              (fn [entity i]
                (ui-category {:ident entity
                              :setProjectLocal (:setProjectLocal props)
                              :projectLocal (:projectLocal props)
                              :parent (id)}))]]]]]))

(def ui-category (comp/comp-factory Category AppContext))
