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
  (let [vars (-> data
                 (dissoc :post/author)
                 (dissoc :post/comments))]
    (cu/execute-gql-mutation-simple ctx "Post" vars {:check-session? true})))

(defc Post [this {:post/keys [id body comments parentID created {author [:ceramic-account/id {:ceramic-account/user [:user/id]}]}
                              {pageInfo [:pageInfo/hasNextPage]}]
                  :or {id (utils/uuid) body "Default content" comments []}}]
  #jsx [:div {:class "flex flex-col overflow-y-auto overflow-x-hidden scroll-x-none max-h-[30vh] w-full max-w-full pt-1 pl-1 pr-2"}
        [Show {:when (not (u/uuid? (id)))
               :fallback #jsx [:form {:class "flex flex-col gap-3"
                                      :onSubmit (fn [e] (.preventDefault e)
                                                  (add-post-remote ctx (data)))}
                               [ta/textarea {:label "Name"
                                             :placeholder "Title"
                                             :value body
                                             :on-change #(comp/set! this :post/body %)}]
                               [b/button {:title "Submit"}]]}
         [:div {:class "flex flex-row gap-3"}
          [user/ui-user {:& {:user/id (or (:ceramic-account/user (author)) (u/uuid))
                             :user/ethereum-address (nth (string/split (:ceramic-account/id (author)) ":") 4)}}]
          [:div {:class "flex flex-col max-w-full w-full overflow-x-hidden"}
           [:p {:class "dark:text-gray-700 text-sm font-bold"} (created)]
           [:p {:class "w-full break-words"} (body)]]]]
        #_[in/input {:placeholder "Add comment ..."
                     :copy false
                     :left-icon (fn [] #jsx [:div {:class "i-tabler-chevron-right"}])
                     :value #(:new-comment (local))
                     :on-change #(setLocal (assoc (local) :new-comment (u/e->v %)))}]
        [Show {:when (> (count (comments)) 0)}
         [:h3 {:class "font-bold text-md mb-2"} "Comments"]
         #_[For {:each (comments)}
            (fn [comment _]
              #jsx [comment/ui-comment comment])]]])

(def ui-post (comp/comp-factory Post AppContext))
