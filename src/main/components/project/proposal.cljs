(ns  main.components.project.proposal
  (:require ["solid-js" :refer [createSignal Show For createMemo]]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/button.cljs" :as b]
            ["../blueprint/indicator.cljs" :as i]
            ["./proposal_modal.cljs" :as modal]
            ["flowbite" :refer [initModals]]
            ["../blueprint/badge.cljs" :as ba]
            ["../../composedb/util.cljs" :as cu]
            ["../../comp.cljs" :as comp]
            ["../../utils.cljs" :as u]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn vote-count-query [id type]
  (str "query { voteCount(filters: {where: {parentID: {equalTo: \"" id "\"}, type: {equalTo: " (get {:up "UP" :down "DOWN"} type) "}}}) }"))

(defn vote-mutation [id type]
  (str "mutation {
              setVote(input: {content: {parentID: \"" id "\", type: " (get {:up "UP" :down "DOWN"} type)" }} {
                            document {
                                       id
                                     }
                })
              }"))

(defc Proposal [this {:proposal/keys [id name {vote-count [:up :down]}] :or {id (u/uuid) name "Proposal" vote-count {:up 0 :down 0}}}]
  (let [somet (createMemo (fn [] (when (id) (initModals))))]
    #jsx [:button {:class "h-10 dark:text-white relative border-indigo-700 dark:border-indigo-600 border-2 dark:text-white
                        rounded-md flex gap-2 items-center hover:(ring-blue-500 ring-1) cursor-pointer p-1"
                   :data-modal-toggle "planner-modal"
                   :data-modal-target "planner-modal"
                   :onClick #((:setProjectLocal props) (assoc-in ((:projectLocal props)) [:modal] {:comp :proposal
                                                                                                   :visible? true
                                                                                                   :ident [:proposal/id (id)]}))}
          [:h2 {:class "font-bold px-2"} (name)]
          #_[ba/badge {:title "Dev"}]
          [:span {:class "flex w-fit justify-items-end gap-1"}
           (str 0 #_(:up (vote-count)))
           [:div {:class "dark:hover:text-white dark:text-gray-400"
                  :onClick #(do
                              (comp/mutate! this {:add {:parentID (id) :type :up}
                                                  :remote {:query (vote-count-query (id) :up)
                                                           :vals {}}})
                              (cu/execute-gql-mutation ctx (vote-mutation (id) :up) {}))}
            [:div {:class "i-tabler-arrow-up"}]]
           (str 0 (:down (vote-count)))
           [:div {:class "dark:hover:text-white dark:text-gray-400"
                  :onClick #(do
                              (comp/mutate! this {:add {:parentID (id) :type :down}
                                                  :remote {:query (vote-count-query (id) :down)
                                                           :vals {}}})
                              (cu/execute-gql-mutation ctx (vote-mutation (id) :down) {}))}
            [:div {:class "i-tabler-arrow-down"}]]]]))

(def ui-proposal (comp/comp-factory Proposal AppContext))
