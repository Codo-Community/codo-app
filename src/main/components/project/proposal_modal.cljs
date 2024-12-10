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
            ["../editor_context.cljs" :refer [EditorContext]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../tiptap/editor.cljs" :as tt])
  (:require-macros [sqeave :refer [defc]]))

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
  (let [vars (dissoc  (sqeave/remove-ns data) :author)
        vars (assoc vars :description description)
        vars (dissoc vars :votes)
        vars (dissoc (dissoc vars :id) :posts)
        vars (if-not (sqeave/uuid? id) (dissoc vars :created) vars)
        vars {:i {:content (sqeave/drop-false vars)}}
        v (println "id:" id)

        vars (if-not (sqeave/uuid? id)
               (assoc-in  vars [:i :id] id) vars)

        vars (sqeave/drop-false vars)
        mutation (if (sqeave/uuid? id)
                   {:name "createProposal"
                    :fn create-mutation}
                   {:name "updateProposal"
                    :fn update-mutation})]
    (println "v: " vars)
    (csqeave/execute-gql-mutation ctx
                             (:fn mutation)
                             vars
                             (fn [res]
                               (println "res: " res)
                               (let [stream-id (:proposal/id res)]
                                 (sqeave/add! ctx res)
                                 (sqeave/swap-uuids! ctx [:proposal/id id] stream-id)
                                 )))))

(defn remove-proposal-remote [ctx id]
  (csqeave/execute-gql-mutation ctx update-mutation
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
                           :or {id (sqeave/uuid) author "Bramaputra" name "Proposal" created (.toLocaleDateString (js/Date.) "sv")
                                status :EVALUATION posts []}
                           :local {new-post nil}}]
  (let [[open? setOpen] (createSignal true)
        {:keys [element menu comp]} (useContext EditorContext)]
    #jsx [:div {:class "dark:bg-black pt-4 pl-4 pb-4 border-1 border-zinc-400 rounded-lg lt-md:w-[90vw]
                        lt-md:w-[90vw] md:max-w-[80vw] 2xl:max-w-[60vw] max-h-[90vh] overflow-hidden"}
          [:div {:class "pr-4"}
           [:div {:class "text-lg font-bold flex flex-col gap-2"}
            [:span {:class "flex flex-row gap-2 items-center w-full"}
             (str "Proposal ")
             [:text {:class "dark:text-purple-600 text-purple-800"}  "#" (sqeave/trunc-id (id))]
             [:span {:class "flex flew-row justify-end w-full"}
              [b/button {:icon "i-tabler-x"
                         :on-click #(.preventDefault %)
                         :data-modal-hide "planner-modal"}]]]
            [:text {:class "truncate mb-2"} (str (name))]]
           [Show {:when (not (sqeave/uuid? (id)))}
            [:div {:class "grid grid-cols-2 w-full gap-2 items-center justify-items-stretch pb-2"}
             [:h1 {:class "text-lg font-bold"} "Votes"]
             [:div {:class ""} "Your vote: " (or (votes) "Vote below")]
             [:div {}
              [:span {:class "flex w-fit gap-2 items-center justify-end"}
               [:text {} (count-up)]
               [:button {:class "dark:text-green-400 dark:hover:text-green-200"
                         :onClick #(do
                                     #_(sqeave/mutate! this {:add {:parentID (id) :type :up}})
                                     (csqeave/execute-gql-mutation ctx (vote-mutation (id) :up) {}
                                                              (fn [r] (t/add! ctx r {:replace [:proposal/id (id) :proposal/votes]}))))}
                " Up vote"]]
              [:span {:class "flex w-fit col-start-1 gap-2 items-center justify-end"}
               (str (count-down) #_(:down (vote-count)))
               [:button {:class "dark:text-red-400 dark:hover:text-red-200"
                         :onClick #(do
                                     #_(sqeave/set-field! this {:add {:parentID (id) :type :down}})
                                     (csqeave/execute-gql-mutation ctx (vote-mutation (id) :down) {}
                                                              (fn [r] (t/add! ctx r {:replace [:proposal/id (id) :proposal/votes]}))))}
                " Down vote"]]]]]
           [:div {:class "max-h-[80vh] min-h-[40vh] pr-4"}
            [:hr {:class "border-zinc-400 mb-1"}]
            [:form {:class "flex flex-col gap-2"
                    :onSubmit (fn [e] (.preventDefault e) (add-proposal-remote ctx (data)))}
             [Show {:when (sqeave/viewer? this (author))}
              [in/input {:label "Name"
                         :placeholder "Title"
                         :value name
                         :on-change #(sqeave/set! this :proposal/name %)}]]
             [tt/editor {:& {:element element
                             :menu menu
                             :label "Description"
                             :comp comp
                             :editable? #(sqeave/viewer? this (author))
                             :on-html-change (fn [html]
                                               (sqeave/set! this :proposal/description html))}}]
             [:span {:class "flex w-full gap-2 mx-auto flex-stretch mb-3"}
              [Show {:when (sqeave/viewer? this (author))}
               [b/button {:title "Submit"
                          :data-modal-hide "planner-modal"}]
               [b/button {:title "Delete"
                          :extra-class "!dark:border-red-500 !dark:text-red-500 !dark:hover:border-red-400 !active:ring-red-400"
                          :on-click #(do (.preventDefault %)
                                         (println "id: " (:parent-id props))
                                         (sqeave/mutate! this {:remove [:proposal/id (id)]
                                                             :from [:category/id (:parent props) :category/proposals]
                                                             :cdb true})
                                         (when (not (sqeave/uuid? (id)))
                                           (remove-proposal-remote ctx (id))))
                          :data-modal-hide "planner-modal"}]]]]
            [Show {:when (not (sqeave/uuid? (id)))}
             [:div {:class "flex flex-col gap-2"}
              [:h1 {:class "text-lg font-bold"} "Discussion"]
              [:hr {:class "border-zinc-400"}]
              [b/button {:title "Add Post"
                         :extra-class "mb-2"
                         :on-click (fn [e] (.preventDefault e)
                                     (let [new-post (merge (post/PostClass.new-data) {:post/parentID (id)
                                                                                 :post/created (.toISOString (js/Date.))
                                                                                 :post/body "" #_(:new-post (local))
                                                                                 :post/author (sqeave/viewer-ident this)})]
                                       #_(setLocal (assoc (local) :new-post nil))
                                       (sqeave/mutate! this {:add new-post
                                                           :append [:proposal/id (id) :proposal/posts]})
                                       #_(post/add-post-remote ctx new-post)))}]]]
            [:div {:class "max-h-[30vh] overflow-y-auto overflow-x-hidden"}
             [For {:each (reverse (posts))}
              (fn [p _]
                #jsx [post/Post {:& {:ident p}}])]]]]]))
