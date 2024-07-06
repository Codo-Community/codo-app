(ns  main.components.project.proposal
  (:require ["solid-js" :refer [createSignal Show For createMemo useContext]]
            ["../utils.cljs" :as utils]
            ["../comp.cljs" :as comp]
            ["../utils.cljs" :as u]
            ["./user.cljs" :as user]
            ["./blueprint/textarea.cljs" :as ta]
            ["./blueprint/button.cljs" :as b]
            ["../composedb/util.cljs" :as cu]
            ["./comment.cljs" :as comment]
            ["../Context.cljs" :refer [AppContext]]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

(def create-mutation
  "mutation createPost($i: CreatePostInput!){
     createPost(input: $i) {
       document { id }
     }
}")

(def update-mutation
  "mutation updatePost($i: UpdatePostInput!){
     updatePost(input: $i) {
       document { id }
     }
}")

(defn add-post-remote [ctx {:post/keys [id name description parentID created] :as data}]
  (let [vars (dissoc  (u/remove-ns data) :author)
        vars (dissoc (dissoc vars :id) :comments)
        vars {:i {:content (u/drop-false vars)}}
        v (println "id:" id)

        vars (if-not (u/uuid? id)
               (assoc-in  vars [:i :id] id) vars)

        vars (u/drop-false vars)
        mutation (if (u/uuid? id)
                   {:name "createPost"
                    :fn create-mutation}
                   {:name "updatePost"
                    :fn update-mutation})]
    (println "v: " vars)
    (cu/execute-gql-mutation ctx
                             (:fn mutation)
                             vars)))

(defc Post [this {:post/keys [id body comments parentID created {author [:ceramic-account/id]}]
                  :or {id (utils/uuid) body "Default content" comments []}}]
  #jsx [:div {:class "border border-indigo-700 rounded-md p-2 mb-4 flex flex-col p-3 overflow-y-auth overflow-x-hidden"}
        [Show {:when (not (u/uuid? (id)))
               :fallback #jsx [:form {:class "flex flex-col gap-3"
                                      :onSubmit (fn [e] (.preventDefault e) (add-post-remote ctx (data)))}
                               [ta/textarea {:label "Name"
                                             :placeholder "Title"
                                             :value body
                                             :on-change #(comp/set! this :post/body %)}]
                               [b/button {:title "Submit"}]]}
         [:div {:class "flex flex-col gap-3"}
          [:p {:class ""} (created)]
          #_(str (:ceramic-account/user (author)))
          [user/ui-user {:& {:user/id (u/uuid)
                             :user/ethereum-address (nth (string/split (author) ":") 4)}}]
          [:p {:class ""} (body)]]]
        [Show {:when (> (count (comments)) 0)}
         [:h3 {:class "font-bold text-md mb-2"} "Comments"]
         #_[For {:each (comments)}
            (fn [comment _]
              #jsx [comment/ui-comment comment])]]])

(def ui-post (comp/comp-factory Post AppContext))
