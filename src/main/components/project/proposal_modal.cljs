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
        vars (dissoc vars :votes)
        vars (dissoc (dissoc vars :id) :posts)
        vars (if-not (u/uuid? id) (dissoc vars :created) vars)
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
                             #_(fn [res]
                               (println "res: " res)
                               (let [stream-id (:proposal/id res)]
                                 #_(t/swap-uuids! ctx id stream-id)
                                 (t/add! ctx res))))))

(defn remove-proposal-remote [ctx id]
  (cu/execute-gql-mutation ctx update-mutation
                           {:i {:content {}
                                :options {:shouldIndex false}
                                :id id}} (fn [r])))


(defn query [id acc-id]
  (str "query {
  node(id: \""  id  "\") {
    ... on Proposal {
      id
      name
      votes(
        account: \"" acc-id "\"
        first: 1
      ) {
        edges {
          node {
            id
            type
          }
        }
      }
      posts(last: 10) {
        edges {
          node {
            id
            author {
              id
            }
            created
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
        pageInfo { hasNextPage }
      }
      created
      description
    }
  }
}"))

(defn vote-mutation [id type]
  (str "mutation {
              setVote(input: {content: {parentID: \"" id "\", type: " (get {:up "UP" :down "DOWN"} type) " }}) {
                            document {
                                       id type
                                     }
                }
              }"))

(defc ProposalModal [this {:proposal/keys [id description name created status parentID count-up count-down
                                           {author [:ceramic-account/id]} {votes [:vote/type]}
                                           posts] :as data
                           :or {id (u/uuid) author "Bramaputra" name "Proposal" created (.toLocaleDateString (js/Date.) "sv")
                                status :EVALUATION posts []}
                           :local {new-post nil}}]
  (let [[open? setOpen] (createSignal true)
        {:keys [element menu comp]} (useContext EditorContext)]
    #jsx [:div {:class "dark:bg-black pt-4 pl-4 pb-4 border-1 border-zinc-400 rounded-lg lt-md:w-[90vw]
                        md:min-w-[50vw] md:max-w-[90vw] max-h-[90vh] overflow-hidden"}
          [:div {:class "pr-4"}
           [:div {:class "text-lg font-bold flex flex-col gap-2"}
            [:span {:class "flex flex-row gap-2 items-center w-full"}
             (str "Proposal ")
             [:text {:class "dark:text-purple-600 text-purple-800"}  "#" (u/trunc-id (id))]
             [:span {:class "flex flew-row justify-end w-full"}
              [b/button {:icon "i-tabler-x"
                         :on-click #(.preventDefault %)
                         :data-modal-hide "planner-modal"}]]]
            [:text {:class "truncate mb-2"} (str (name))]]
           [Show {:when (not (u/uuid? (id)))}
            [:div {:class "grid grid-cols-2 w-full gap-2 items-center justify-items-stretch pb-2"}
             [:h1 {:class "text-lg font-bold"} "Votes"]
             [:div {:class ""} "Your vote: " (or (first (votes)) "Vote below")]
             [:div {}
              [:span {:class "flex w-fit gap-2 items-center justify-end"}
               [:text {} (count-up)]
               [:button {:class "dark:text-green-400 dark:hover:text-green-200"
                         :onClick #(do
                                     #_(comp/mutate! this {:add {:parentID (id) :type :up}})
                                     (cu/execute-gql-mutation ctx (vote-mutation (id) :up) {}
                                                              (fn [r] (let [res (-> r :setVote :document)]
                                                                        (println "rrr: " res " r: " r)
                                                                        (t/add! ctx (u/add-ns res))))))}
                " Up vote"]]
              [:span {:class "flex w-fit gap-2 items-center justify-end"}
               (str (count-down) #_(:down (vote-count)))
               [:button {:class "dark:text-red-400 dark:hover:text-red-200"
                         :onClick #(do
                                     #_(comp/set-field! this {:add {:parentID (id) :type :down}})
                                     (cu/execute-gql-mutation ctx (vote-mutation (id) :down) {}
                                                              (fn [r] (let [res (-> r :setVote :document)]
                                                                        (t/add! ctx (u/add-ns) res)))))}
                " Down vote"]]]]]
           [:div {:class "max-h-[80vh] min-h-[40vh] overflow-auto"}
            [:hr {:class "border-zinc-400 mb-1"}]
            [:form {:class "flex flex-col gap-2"
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
             [:span {:class "flex w-full gap-2 mx-auto flex-stretch mb-3"}
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
                          :data-modal-hide "planner-modal"}]]]]
            [Show {:when (not (u/uuid? (id)))}
             [:div {:class "flex flex-col gap-2"}
              [:h1 {:class "text-lg font-bold"} "Discussion"]
              [:hr {:class "border-zinc-400"}]
              [b/button {:title "Add Post"
                         :extra-class "mb-2"
                         :on-click (fn [e] (.preventDefault e)
                                     (let [new-post (merge (post/Post.new-data) {:post/parentID (id)
                                                                                 :post/created (.toISOString (js/Date.))
                                                                                 :post/body "" #_(:new-post (local))
                                                                                 :post/author (comp/viewer-ident this)})]
                                       #_(setLocal (assoc (local) :new-post nil))
                                       (comp/mutate! this {:add new-post
                                                           :append [:proposal/id (id) :proposal/posts]})
                                       #_(post/add-post-remote ctx new-post)))}]]]
            [:div {:class "max-h-[30vh] overflow-y-auto overflow-x-hidden"}
             [For {:each (reverse (posts))}
              (fn [p _]
                #jsx [post/ui-post {:& {:ident p}}])]]]]]))

(def ui-proposal-modal (comp/comp-factory ProposalModal AppContext))
