(ns main.components.wizards.new-project.main
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["@solidjs/router" :refer [A useParams]]
            ["./info_step.cljs" :refer [BasicInfoStepClass]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../../stepper.cljs" :as s])
  (:require-macros [sqeave :refer [defc]]))

(defc WizardNewProject [this {:keys [:component/id project] :as data
                              :or {id :project-wizard project (BasicInfoStepClass.new-data)}}]
  (let [[local setLocal] (createSignal {:step :info
                                        :step-route {:info {:back nil
                                                            :forward "/contract"}
                                                     :contract {:back "/"
                                                                :forward "/wallet"}}
                                        :steps [{:id :info
                                                 :heading "Project Information"
                                                 :details "Enter basic project information."
                                                 :completed? false
                                                 :active? true
                                                 :icon :clipboard-document-list}
                                                {:id :contract
                                                 :heading "Deploy Contract"
                                                 :details "Deploy the Project to the Blockchain."
                                                 :completed? false
                                                 :active? false
                                                 :icon :cube}]})
        params (useParams)]
    #jsx [:div {:class "flex justify-center w-screen px-4"}
          [:div {:class "mt-4 w-fit h-fit lt-md:hidden"}
           [s/Stepper {:& (local)}]]
          [:div {:class "relative"}
           [:h1 {:class "text-2xl mb-4 mt-2"} "Create Project"]

           props.children

           [Show {:when (not (sqeave/uuid? (:id params)))}
            [:div {:class "flex flex-inline mt-6"}
             [Show {:when (not (nil? (get-in (:step-route (local)) [(:step (local)) :back])))}
              [A {:href (str "/wizards/new-project/" (:id params) (get-in (:step-route (local)) [(:step (local)) :back]))}
               [:div {:class "i-tabler-arrow-left"}]]]
             [Show {:when (not (nil? (get-in (:step-route (local)) [(:step (local)) :forward])))}
              [A {:href (str "/wizards/new-project/" (:id params) (get-in (:step-route (local)) [(:step (local)) :forward]))
                                        ;:onClick #(setLocal :step )
                  }
               [:div {:class "i-tabler-arrow-right"}]]]]]]]))
