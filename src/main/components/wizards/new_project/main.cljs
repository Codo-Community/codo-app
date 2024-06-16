(ns main.components.wizards.new-project.main
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["@solidjs/router" :refer [A useParams]]
            ["flowbite" :as fb]
            ["../../../utils.cljs" :as u]
            ["../../../Context.cljs" :refer [AppContext]]
            ["../../stepper.cljs" :as s]))

(defn add-new-proj []
  )

(defn WizardNewProject [props]
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
    #jsx [:div {:class "flex justify-center w-screen"}
          [:div {:class "mt-4 w-fit h-fit"}
           [s/ui-stepper {:& (local)}]]
          [:div {:class "ml-24 relative"}
           [:div {:class "my-4"}
            [:h1 {:class "text-2xl mb-4"} "Create Project"]]

           props.children

           [Show {:when (not (u/uuid? (:id params)))}
            [:div {:class "flex flex-inline mt-6"}
             [Show {:when (not (nil? (get-in (:step-route (local)) [(:step (local)) :back])))}
              [A {:href (str "/wizards/new-project/" (:id params) (get-in (:step-route (local)) [(:step (local)) :back]))}
               [:div {:class "i-tabler-arrow-left"}]]]
             [Show {:when (not (nil? (get-in (:step-route (local)) [(:step (local)) :forward])))}
              [A {:href (str "/wizards/new-project/" (:id params) (get-in (:step-route (local)) [(:step (local)) :forward]))
                                        ;:onClick #(setLocal :step )
                  }
               [:div {:class "i-tabler-arrow-right"}]]]]]]]))

(def default WizardNewProject)
