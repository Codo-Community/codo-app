(ns main.components.blueprint.stepper
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["./step.jsx" :as s :refer [ui-step]]
            ["../../Context.mjs" :refer [AppContext]]
            ["../../comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defc Stepper [this {:keys [steps click-fns]}]
  #jsx [:ol {:class "select-none relative text-gray-500 border-l border-gray-200
                     dark:border-gray-700 dark:text-gray-400"}
        (println "da1 " (:steps (:children (data))))
        #jsx [Index {:each (:steps (:children (data)))}
              (fn [step i]
                (str step)
                #jsx [s/ui-step step #_{:onClick (get click-fns (:step/id step))}])]])

(def ui-stepper (comp/comp-factory Stepper AppContext))
