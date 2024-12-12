(ns main.components.blueprint.stepper
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["./step.cljs" :as s :refer [Step]]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defc Stepper [this {:keys [steps click-fns]}]
  #jsx [:ol {:class "select-none relative text-gray-500 border-l border-gray-200
                     dark:border-gray-700 dark:text-gray-400"}
        #_(println "da1 " (:steps (data)))
        #jsx [For {:each (:steps (this.data))}
              (fn [step i]
                #jsx [s/Step {:& (step)} ])]])

;{:onClick (get click-fns (:step/id step))}
