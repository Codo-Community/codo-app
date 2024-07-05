(ns  main.components.project.proposal
  (:require ["solid-js" :refer [createSignal Show For useContext]]
            ["../../composedb/util.cljs" :as cu]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/button.cljs" :as b]
            ["../blueprint/input.cljs" :as in]
            ["../blueprint/tooltip.cljs" :as to]
            ["../post.cljs" :as post]
            ["../blueprint/textarea.cljs" :as ta]
            ["../category/category.cljs" :as c]
            ["lodash" :refer [escape unescape]]
            ["../editor_context.cljs" :refer [EditorContext]]
            ["../../comp.cljs" :as comp]
            ["../../utils.cljs" :as u]
            ["../tiptap/editor.cljs" :as tt]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def create-mutation
  "mutation createProposal($i: CreateProposalInput!){
     createProposal(input: $i){
       document { name description parentID created color description status }
     }
}")

(def update-mutation
  "mutation updateProposal($i: UpdateProposalInput!){
     updateProposal(input: $i){
       document { id name color description parentID created }
     }
}")


(defn add-proposal-remote [ctx {:proposal/keys [id name description parentID created] :as data}]
  (let [vars (dissoc  (u/remove-ns data) :author)
        vars (dissoc vars :id)
        vars (assoc vars :description description)
        vars {:i {:content (u/drop-false vars)}}
        v (println "id:" id)

        vars (if-not (u/uuid? id)
               (assoc-in  vars [:i :id] id) vars)

        vars (u/drop-false vars)
        mutation (if (u/uuid? id)
                   {:name "createProposal"
                    :fn create-mutation}
                   {:name "updateProposal"
                    :fn update-mutation})]
    (println "v: " vars)
    (cu/execute-gql-mutation ctx
                             (:fn mutation)
                             vars
                             (fn [r]
                               (let [proposal (u/nsd (get-in r [(:name mutation) :document]) :proposal)
                                     stream-id (:proposal/id proposal)]
                                        ; TODO: fix changing uuid with stream-id here
                                        ; so the data will be like {:proposal/id #uuid, :proposal/name "xyz" ...}
                                        ; we need to do a function which deep recursively updates the normalized state whenever [:proposal/id #uuid] shows up as a reference
                                        ; and inside the :proposal/id table {:proposal/id {:#uuid-xyz {:proposal/id :#uuid-xyz ... -> :proposal/id stream-id
                                 #_(normad/swap-uuids! ctx id stream-id)

                                 #_(when (and (u/uuid? id) parent-id)
                                     (cu/execute-gql-mutation ctx create-link-mut
                                                              {:i {:content {:parentID parent-id :childID (:proposal/id proposal)}}}
                                                              (fn [r] #_(println "re: " r)))))))))


;; TODO: need to add viewer id etc here

(defn remove-proposal-remote [ctx id]
  (cu/execute-gql-mutation ctx update-mutation
                           {:i {:content {}
                                :options {:shouldIndex false}
                                :id id}} (fn [r])))


(defc ProposalModal [this {:proposal/keys [id description name created status {author [:id :isViewer]} posts] :as data
                           :or {id (u/uuid) author "Bramaputra" name "Proposal" created (.toLocaleDateString (js/Date.) "sv")
                                status :EVALUATION}}]
  (let [[open? setOpen] (createSignal true)
        {:keys [element menu comp]} (useContext EditorContext)]
    #jsx [:div {:class "dark:bg-black p-4 border-1 border-zinc-400 rounded-lg"}
          [:div {:class "flex justify-between items-center"}
           [:h1 {:class "text-lg font-bold"} "Proposal"]
           [to/tooltip {:content "Close"
                        :position "top"
                        :extra-class "cursor-pointer"
                        :on-click #(do (.preventDefault %)
                                       (setOpen false))} [:i {:class "fas fa-times"}]]]
          [:hr {:class "border-zinc-400"}]
          [:form {:class "flex flex-col gap-3"
                  :onSubmit (fn [e] (.preventDefault e) (add-proposal-remote ctx (data)))}
           [:span {:class "flex  w-full gap-3"}
            [in/input {:label "Name"
                       :placeholder "Title"
                       :value name
                       :on-change #(comp/set! this :proposal/name %)}]]
           [tt/editor {:& {:element element
                           :menu menu
                           :comp comp
                           :on-html-change (fn [html]
                                             (comp/set! this :proposal/description html))}}]
           [:h1 {:class "text-lg font-bold"} "Posts"]
           [:hr {:class "border-zinc-400"}]
           [:span {:class "flex w-full gap-3"}
            [b/button {:title "Add Post"
                       :on-click #(do (.preventDefault %)
                                      (comp/mutate! this {:add (post/Post.new-data)
                                                          :append [:proposal/id (id) :proposal/posts]}))}]]
           [For {:each (posts)}
            (fn [post _]
              #jsx [post/ui-post {:& {:ident post}}])]
           [:span {:class "flex w-full gap-3 mx-auto flex-stretch"}
            [b/button {:title "Submit"
                       :data-modal-hide "planner-modal"}]
            [b/button {:title "Delete"
                       :extra-class "!dark:border-red-500 !dark:text-red-500 !dark:hover:bg-red-400 !active:ring-red-400"
                       :on-click #(do (.preventDefault %)
                                      (println "id: " (:parent-id props))
                                      (comp/mutate! this {:remove [:proposal/id (id)]
                                                          :from [:category/id (:parent props) :category/proposals]
                                                          :cdb true})
                                      (when (not (u/uuid? (id)))
                                        (remove-proposal-remote ctx (id))))
                       :data-modal-hide "planner-modal"}]

            [b/button {:title "Close"
                       :on-click #(.preventDefault %)
                       :data-modal-hide "planner-modal"}]]]]))

(def ui-proposal-modal (comp/comp-factory ProposalModal AppContext))
