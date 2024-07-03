(ns  main.components.project.proposal
  (:require ["solid-js" :refer [createSignal Show For]]
            ["../../composedb/util.cljs" :as cu]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/button.cljs" :as b]
            ["../blueprint/input.cljs" :as in]
            ["../blueprint/textarea.cljs" :as ta]
            ["../category/category.cljs" :as c]
            ["../../comp.cljs" :as comp]
            ["../../utils.cljs" :as u]
            ["../tiptap/editor.cljs" :as tt]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def update-mutation
  "mutation createProposal($i: CreateProposalInput!){
     createProposal(input: $i){
       document { id name color description }
     }
}")

(def create-mutation
  "mutation updateProposal($i: UpdateProposalInput!){
     updateProposal(input: $i){
       document { name color description }
     }
}")


(defn add-proposal-remote [ctx {:propsal/keys [id name description parentID] :as data}]
  (let [vars {:name name :description description} #_(utils/remove-ns data)
        vars (dissoc vars :id)
        vars {:i {:content (u/drop-false vars)}}
        vars (if-not (u/uuid? id)
               (assoc-in  vars [:i :id] id) vars)
        vars (u/drop-false vars)
        mutation (if (u/uuid? id)
                   {:name "createProposal"
                    :fn create-mutation}
                   {:name "updateProposal"
                    :fn update-mutation})]
    (println vars)
    (cu/execute-gql-mutation ctx
                             (:fn mutation)
                             vars
                             (fn [r]
                               (let [proposal (utils/nsd (get-in r [(:name mutation) :document]) :proposal)
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

(defc ProposalModal [this {:proposal/keys [id author description name parentID] :or {id (u/uuid) author "Bramaputra" name "Proposal" }}]
  (let [[open? setOpen] (createSignal true)]
    #jsx [:div {}
          [tt/editor {:& {:on-html-change #(comp/set! this :proposal/description %)}}]
          [:div {:class "flex justify-end gap-3"}
           #_[b/button {:title "Cancel"
                        :on-click #(setOpen false)}]
           [b/button {:title "Submit"
                      :on-click #(add-proposal-remote ctx (data))}]]
          #_[:form {:class "flex flex-col gap-3"
                    :onSubmit (fn [e] (.preventDefault e) (add-proposal-remote ctx (data) nil))}
             [:span {:class "flex  w-full gap-3"}
              [in/input {:label "Name"
                         :placeholder "Title"
                         :value name
                         :on-change #(comp/set! this :proposal/name %)}]]
             [ta/textarea {:title "Description"
                           :value description
                           :on-change #(comp/set! this :proposal/description %)}]
             [:span {:class "flex w-full gap-3"}
              [b/button {:title "Submit"}]]]]))

(def ui-proposal-modal (comp/comp-factory ProposalModal AppContext))
