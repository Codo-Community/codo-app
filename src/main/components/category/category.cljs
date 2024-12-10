(ns components.category
  (:require ["solid-js" :refer [Show createSignal useContext onMount createMemo Suspense createEffect createResource]]
            #_["@solid-primitives/active-element" :refer [createFocusSignal]]
            ["../blueprint/input.cljs" :as in]
            ["../../composedb/util.cljs" :as cu]
            ["../project/proposal.cljs" :as pr]
            ["./query.cljs" :as cq]
            ["./menu.cljs" :as cm]
            ["solid-spinner" :as spinner]
            ["./context.cljs" :refer [FilterContext]]
            ["@solidjs/router" :refer [cache createAsync useParams]]
            ["./category_link.cljs" :as  cl]
            ["../blueprint/button.cljs" :as b]
            ["../../composedb/client.cljs" :as cli]
            ["~/main/Context.cljs" :refer [AppContext]]
            ["flowbite" :refer [initModals]]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

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


(defn remove-category-remote [ctx link]
  (cu/execute-gql-mutation ctx update-link-mut
                           {:i {:content {}
                                :options {:shouldIndex false}
                                :id link}} (fn [r])))

(defn add-category-remote [ctx {:category/keys [id name color description] :as data} parent-id link]
  (let [vars (dissoc (sqeave/remove-ns data) :creator)
        vars (dissoc vars :id)
        vars {:i {:content (utils/drop-false vars)}}
        vars (if-not (sqeave/uuid? id)
               (assoc-in vars [:i :id] id)
               (assoc-in vars [:i :content :created] (.toLocaleDateString (js/Date.) "sv")))
        vars (sqeave/drop-false vars)
        mutation (if (sqeave/uuid? id)
                   {:name "createCategory"
                    :fn create-mutation}
                   {:name "updateCategory"
                    :fn update-mutation})]
    (println vars)
    (cu/execute-gql-mutation ctx
                             (:fn mutation)
                             vars
                             (fn [category]
                               (let [stream-id (:category/id category)]
                                 (if (sqeave/uuid? id)
                                   (sqeave/swap-uuids! ctx [:category/id id] stream-id))
                                 (when (and (sqeave/uuid? id) parent-id)
                                   (cu/execute-gql-mutation ctx create-link-mut
                                                            {:i {:content {:parentID parent-id :childID stream-id}}}
                                                            (fn [r]
                                                              (let [stream-id (:category-link/id r)]
                                                                (if (sqeave/uuid? link)
                                                                  (sqeave/swap-uuids! ctx link stream-id)))))))))))

(declare Category)

(defn load-category [ctx id]
  (cu/execute-gql-query ctx (cq/simple id) {} (fn [r]
                                                (let [ps (mapv #(:proposal/id %) (get r :category/proposals))
                                                      a (println "asd: " r (vector? (get r :category/children)))
                                                      #_r #_(if-not (vector? (get r :category/children))
                                                          (assoc r :category/children []))]
                                                  (sqeave/add! ctx r {:check-session? false})
                                                  (mapv #(cu/execute-gql-query ctx (pr/vote-count-query % :up) {}
                                                                               (fn [r] (sqeave/add! ctx {:proposal/id % :proposal/count-up (:voteCount r)} {:check-session? false}))) ps)
                                                  (mapv #(cu/execute-gql-query ctx (pr/vote-count-query % :down) {}
                                                                               (fn [r] (sqeave/add! ctx {:proposal/id % :proposal/count-down (:voteCount r)} {:check-session? false}))) ps)))))

(def load-category-c (cache (fn [id]
                              (let [ctx (useContext AppContext)]
                                (println "loading category" id)
                                (load-category ctx id)))))

(defn load-category-cache [id]
  (load-category-c id))

(defc Category [this {:category/keys [id name color {children [:category-link/id]}
                                      {creator [:ceramic-account/id]} proposals]
                      :or {id (sqeave/uuid) name "Category" children [] color :gray proposals []}
                      :local {editing? false open? false hovering? false selected nil indent? true show-proposals? true}}]
  (let [filters (useContext FilterContext)
        ;components ()
        ;asd (createResource (fn [] (load-category ctx (id))))
        ]
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
                                 (when (and (not (:open? (local))) (not (sqeave/uuid? (id))))
                                   (load-category ctx (id)))
                                 (setLocal (assoc (local) :open? (not (:open? (local))))))}]
            [Show {:when (not (:editing? (local))) :fallback #jsx [in/input {:placeholder "Name ..."
                                                                             :value name
                                                                             :on-focus-out (fn [e] (setLocal (assoc (local) :editing? false)))
                                                                             :on-change (fn [e]
                                                                                          (setLocal (assoc (local) :editing? false))
                                                                                          (sqeave/set! this (:ident props) :category/name e)
                                        ; TODO: need to auto swap uuids for streamIDs
                                                                                          (add-category-remote ctx (data) (:parent props) (:link props)))}]}
             [:span {:class "flex w-full gap-2"}
              [:button {:data-modal-target "planner-modal"
                        :data-modal-toggle "planner-modal"

                        :onClick #((:setProjectLocal props) (assoc ((:projectLocal props)) :modal {:comp :category
                                                                                                   :props {:parent (:parent props)}
                                                                                                   :ident [:category/id (id)]}))} (name)]
              [:span {:class "flex gap-24 w-full items-end justify-end gap-2"}
               [:button {:class "h-7 w-fit text-sm relative border-gray-700 border-2 dark:text-gray-800
                              rounded-md flex gap-2 mr-10 items-center hover:(ring-blue-500 ring-1) p-1"
                         :onClick #(sqeave/add! ctx {:proposal/id (sqeave/uuid)
                                                :proposal/name "New proposal"
                                                :proposal/author (sqeave/viewer-ident this)
                                                :proposal/created (.toLocaleDateString (js/Date.) "sv")
                                                :proposal/status :EVALUATION
                                                :proposal/parentID (id)}
                                           {:append [:category/id (id) :category/proposals]})} "Add"]]]

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

           #_[cm/CategoryMenu {:&  {:ident (:ident props)
                                        :parent (:parent props)}}]
           [Show {:when (and (sqeave/viewer? this (creator)) (:hovering? (local)) (not (:editing? (local))))}
            [cm/CategoryMenu {:& props}]]]
          [Show {:when (:open? (local))}
           [:div {:class "flex flex-col gap-1"}
            [Show {:when (:show-proposals? (filters))}
             [:div {:class "flex flex-col gap-1"}
              [For {:each (proposals)}
               (fn [proposal i]
                 #jsx [pr/Proposal {:& {:ident proposal
                                           :parent (id)
                                           :projectLocal (:projectLocal props)
                                           :setProjectLocal (:setProjectLocal props)}}])]]]
            [For {:each (reverse (children))}
             (fn [entity i]
               #jsx [cl/CategoryLink {:ident (fn [] [:category-link/id entity])
                                          :setProjectLocal (:setProjectLocal props)
                                          :projectLocal (:projectLocal props)
                                          :parent (id)}])]]]]))
