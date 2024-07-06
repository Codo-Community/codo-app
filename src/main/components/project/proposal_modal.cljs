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
            ["../../transact.cljs" :as t]
            ["../../utils.cljs" :as u]
            ["../tiptap/editor.cljs" :as tt]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def create-mutation
  "mutation createProposal($i: CreateProposalInput!){
     createProposal(input: $i){
       document { id }
     }
}")

(def update-mutation
  "mutation updateProposal($i: UpdateProposalInput!){
     updateProposal(input: $i){
       document { id }
     }
}")

(defn add-proposal-remote [ctx {:proposal/keys [id name description parentID created] :as data}]
  (let [vars (dissoc  (u/remove-ns data) :author)
        vars (assoc vars :description description)
        vars (dissoc (dissoc vars :id) :posts)
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
                             vars)))

(defn remove-proposal-remote [ctx id]
  (cu/execute-gql-mutation ctx update-mutation
                           {:i {:content {}
                                :options {:shouldIndex false}
                                :id id}} (fn [r])))


(defn q [id]
  (str "query {
  node(id: \""  id  "\") {
    ... on Proposal {
      id
      name
      posts(last: 10) {
        edges {
          node {
            id
            author {
              id
            }
            body
            comments(last: 10) {
              edges {
                node {
                  id
                  text
                  author {
                    id
                  }
                  created
                }
              }
            }
          }
        }
      }
      created
      description
    }
  }
}"))

(defc ProposalModal [this {:proposal/keys [id description name created status parentID {author [:ceramic-account/id]} posts] :as data
                           :or {id (u/uuid) author "Bramaputra" name "Proposal" created (.toLocaleDateString (js/Date.) "sv")
                                status :EVALUATION posts []}
                           :local {new-post nil}}]
  (let [[open? setOpen] (createSignal true)
        {:keys [element menu comp]} (useContext EditorContext)]
    #jsx [:div {:class "dark:bg-black pt-4 pl-4 pb-4 border-1 border-zinc-400 rounded-lg lt-md:w-[90vw] md:min-w-[50vw] md:max-w-[50vw] max-h-[90vh] overflow-hidden"}
          [:div {:class "text-lg font-bold"} (str "Proposal #"  (u/trunc-id (id)) ": ")
           [:text {:class "truncate"} (str (name))]]
          [:hr {:class "border-zinc-400"}]
          [:div {:class "max-h-[80vh] min-h-[50vh] overflow-auto pr-4"}
           [:form {:class "flex flex-col gap-3"
                   :onSubmit (fn [e] (.preventDefault e) (add-proposal-remote ctx (data)))}
            [Show {:when (comp/viewer? this (author))}
             [in/input {:label "Name"
                        :placeholder "Title"
                        :value name
                        :on-change #(comp/set! this :proposal/name %)}]]
            [tt/editor {:& {:element element
                            :menu menu
                            :label "Description"
                            :comp comp
                            :editable? #(comp/viewer? this (author))
                            :on-html-change (fn [html]
                                              (comp/set! this :proposal/description html))}}]
            [:span {:class "flex w-full gap-3 mx-auto flex-stretch mb-3"}
             [Show {:when (comp/viewer? this (author))}
              [b/button {:title "Submit"
                         :data-modal-hide "planner-modal"}]
              [b/button {:title "Delete"
                         :extra-class "!dark:border-red-500 !dark:text-red-500 !dark:hover:border-red-400 !active:ring-red-400"
                         :on-click #(do (.preventDefault %)
                                        (println "id: " (:parent-id props))
                                        (comp/mutate! this {:remove [:proposal/id (id)]
                                                            :from [:category/id (:parent props) :category/proposals]
                                                            :cdb true})
                                        (when (not (u/uuid? (id)))
                                          (remove-proposal-remote ctx (id))))
                         :data-modal-hide "planner-modal"}]]

             [b/button {:title "Close"
                        :on-click #(.preventDefault %)
                        :data-modal-hide "planner-modal"}]]]

           [Show {:when  (not (u/uuid? (id)))}
            [:div {}
             [:h1 {:class "text-lg font-bold"} "Posts"]
             [:hr {:class "border-zinc-400"}]
             [:form {:class "" :onSubmit (fn [e] (.preventDefault e)
                                           (let [new-post (merge (post/Post.new-data) {:post/parentID (id)
                                                                                       :post/created (.toISOString (js/Date.))
                                                                                       :post/body (:new-post (local))
                                                                                       :post/author (comp/viewer-ident this)})]
                                             (println "str:" (.toISOString (js/Date.)))
                                             (setLocal (assoc (local) :new-post nil))
                                             (comp/mutate! this {:add new-post
                                                                 :append [:proposal/id (id) :proposal/posts]})
                                             (post/add-post-remote ctx new-post)))}
              [:span {:class "flex w-full gap-3 items-center mb-3"}
               [in/input {:placeholder "Comment on this proposal ..."
                          :copy false
                          :left-icon (fn [] #jsx [:div {:class "i-tabler-chevron-right"}])
                          :value #(:new-post (local))
                          :on-change #(setLocal (assoc (local) :new-post (u/e->v %)))}]]]]]
           [:div {:class "max-h-[80vh] overflow-auto pr-4"}
            [For {:each (posts)}
             (fn [p _]
               #jsx [post/ui-post {:& {:ident p}}])]]]]))

(def ui-proposal-modal (comp/comp-factory ProposalModal AppContext))
