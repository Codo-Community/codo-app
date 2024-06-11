(ns main.components.blueprint.stepper
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["./step.cljs" :as s :refer [ui-step]]
            ["../Context.cljs" :refer [AppContext]]
            ["../comp.cljs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defc Stepper [this {:keys [steps click-fns]}]
  #jsx [:ol {:class "select-none relative text-gray-500 border-l border-gray-200
                     dark:border-gray-700 dark:text-gray-400"}
        (println "da1 " (:steps (data)))
        #jsx [For {:each (:steps (data))}
              (fn [step i]
                #jsx [s/ui-step {:& (step)} ])]])

(def ui-stepper (comp/comp-factory Stepper AppContext))

;{:onClick (get click-fns (:step/id step))}
