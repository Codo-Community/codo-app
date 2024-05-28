(ns co-who.components.evm.function
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../blueprint/button.jsx" :as b]
            ["./input.jsx" :as ein]
            #_[co-blue.icons.chevron-right :refer [chevron-right]]
            ["../../normad.mjs" :as n :refer [add]]
            ["../blueprint/label.jsx" :as l]
            ["../../Context.mjs" :refer [AppContext]]
            ["../../comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defc Function [this {:keys [function/id name type stateMutability
                             {inputs [:internalType :type :name :value]}
                             {outputs [:internalType :type :name :value]}]}]
  (let [{:keys [on-change open]} {:open true}]
    #jsx [:div {}
          [:span {:class "flex inline-flex w-full items-center pb-2 dark:border-gray-600"}
           #_(b/icon-button {:class "dark:text-gray-600"} "cr " #_(chevron-right))
           #_[:button {:onClick (fn [e]
                                  (ein/set-abi-field ctx [:function/id (:function/id (data)) :inputs 0 :value] "1"))} "b"]
           [:h1 {:class "font-bold dark:border-gray-600 border-gray-200 "}
            (str (name))]]
          #jsx [Show {:when open}
                [:div {}
                 #jsx [Show {:when (not (empty? (inputs)))}
                       [:h2 {:class "mb-2 font-bold"} (str "Inputs: ")]
                       #jsx [Index {:each (inputs)}
                             (fn [entry i]
                               #_(println "render fn " (entry))
                               #jsx [ein/input entry #_{:readonly false
                                                        :on-change (fn [e]
                                                                     (ein/set-abi-field ctx [:function/id (:function/id (data)) :inputs i :value] e.target.value))}])]]
                 #_#jsx [Show {:when (not (empty? (:outputs (data))))}
                         [:h2 {:class "mb-2 font-bold text-green"} (str "Outputs: ")]
                         #jsx [For {:each (vec (:outputs (data)))}
                               (fn [entry _]
                                 (ein/input entry {:local/editable? false}))]]]]]))

(def ui-function (comp/comp-factory Function AppContext))
