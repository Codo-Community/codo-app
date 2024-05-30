(ns main.components.wizards.new-project.main
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["solid-icons/hi" :refer [HiOutlineArrowLeft HiOutlineArrowRight]]
            ["@solidjs/router" :refer [A]]
            ["../../blueprint/stepper.jsx" :as s]))

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
                                                 :icon :cube}]})]
    #jsx [:div {:class "flex justify-center"}
          [:div {:class "mt-4 w-fit h-fit"}
           [s/ui-stepper (local)]]
          [:div {:class "ml-24 relative"}
           [:div {:class "my-4"}
            [:h1 {:class "text-2xl mb-4"} "Create Project"]]

           props.children

           [:div {:class "flex flex-inline mt-6"}
            [Show {:when (not (nil? (get-in (:step-route (local)) [(:step (local)) :back])))}
             [A {:href (str "/wizards/new-project" (get-in (:step-route (local)) [(:step (local)) :back]))}
              [HiOutlineArrowLeft]]]
            [Show {:when (not (nil? (get-in (:step-route (local)) [(:step (local)) :forward])))}
             [A {:href (str "/wizards/new-project" (get-in (:step-route (local)) [(:step (local)) :forward]))
                 ;:onClick #(setLocal :step )
                 }
              [HiOutlineArrowRight]]]

            #_(if-let [next (get-in step-route [step :forward])]
                (if-not (tempid/tempid? id)
                  (:div {:class "absolute right-0"}
                        (f/ui-button {:color "gray"
                                      :onClick #(let [next-state (get step-route next)]
                                                  ((:onEnter next-state) this (:comp next-state) {:id id})
                                                  (comp/set-state! this {:active next}))}
                                     (:div :.h-5.w-5 arrow-right)))))]]]))

(def default WizardNewProject)
