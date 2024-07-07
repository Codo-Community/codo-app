(ns  main.components.project.proposal
  (:require ["solid-js" :refer [createSignal Show For createMemo useContext]]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/button.cljs" :as b]
            ["../blueprint/indicator.cljs" :as i]
            ["./proposal_modal.cljs" :as modal]
            ["flowbite" :refer [initModals]]
            ["../blueprint/badge.cljs" :as ba]
            ["../../utils.cljs" :as utils]
            ["../../transact.cljs" :as t]
            ["../../composedb/util.cljs" :as cu]
            ["../../comp.cljs" :as comp]
            ["../editor_context.cljs" :refer [EditorContext]]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn vote-count-query [id type]
  (str "query { voteCount(filters: {where: {parentID: {equalTo: \"" id "\"}, type: {equalTo: " (get {:up "UP" :down "DOWN"} type) "}}}) }"))

(defn vote-mutation [id type]
  (str "mutation {
              setVote(input: {content: {parentID: \"" id "\", type: " (get {:up "UP" :down "DOWN"} type) " }}) {
                            document {
                                       id, type
                                     }
                }
              }"))

(declare Proposal)

(defn load-proposal [ctx id]
  (cu/execute-eql-query ctx {[:proposal/id id] Proposal.query}))

(defc Proposal [this {:proposal/keys [id name description count-up count-down
                                      #_{vote-count [:up :down]} status {author [:id :isViewer]}]
                      :or {id (utils/uuid) name "Proposal" #_vote-count #_{:up 0 :down 0} status :EVALUATION}}]
  (let [somet (createMemo (fn [] (when (id) (initModals))))
        {:keys [element menu comp]} (useContext EditorContext)]
    #jsx [:div {:class "h-10 text-black dark:text-white relative border-zinc-700 dark:border-gray-600 border-2 dark:text-white
                           rounded-md flex gap-2 items-center hover:(ring-blue-500 ring-1) p-1"}
          [:h2 {:class "font-bold px-2  cursor-pointer"
                :data-modal-toggle "planner-modal"
                :data-modal-target "planner-modal"
                :onClick #(do
                            (println "field: " "q")
                            (cu/execute-eql-query ctx {[:proposal/id (id)] modal/ProposalModal.query})
                            ((:setProjectLocal props) (assoc-in ((:projectLocal props)) [:modal] {:comp :proposal
                                                                                                  :props {:parent (:parent props)}
                                                                                                  :ident [:proposal/id (id)]}))
                            (.commands.setContent (comp) (description)))} (name)]
          #_[ba/badge {:title "Dev"}]
          [:span {:class "flex w-fit gap-1 items-center"}
           (str (count-up) #_(:up (vote-count)))
           [:button {:class "dark:text-green-400 text-green dark:hover:text-green-200"
                     :onClick #(do
                                 #_(comp/mutate! this {:add {:parentID (id) :type :up}})
                                 (cu/execute-gql-mutation ctx (vote-mutation (id) :up) {} (fn [r] (println "vote answer: " r))))}
            [:div {:class "i-tabler-arrow-up h-8"}]]
           (str (count-down) #_(:down (vote-count)))
           [:button {:class "dark:text-red-400 text-red dark:hover:text-red-200"
                     :onClick #(do
                                 (comp/mutate! this {:add {:parentID (id) :type :down}})
                                 (cu/execute-gql-mutation ctx (vote-mutation (id) :down) {}))}
            [:div {:class "i-tabler-arrow-down h-8"}]]]]))

(def ui-proposal (comp/comp-factory Proposal AppContext))
