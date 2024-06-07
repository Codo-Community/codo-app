(ns co-who.components.evm.function
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["../blueprint/button.jsx" :as b]
            ["./input.jsx" :as ein]
            ["../../normad.mjs" :as n :refer [add]]
            ["../blueprint/label.jsx" :as l]
            ["../../Context.mjs" :refer [AppContext]]
            ["../../comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defc Function [this {:keys [function/id name type stateMutability
                             {inputs [:internalType :type :name :value]}
                             {outputs [:internalType :type :name :value]}]}]
  (let [[open? setOpen] (createSignal true)]
    #jsx [:div {:class "flex flex-col gap-3"}
          [:span {:class "flex inline-flex w-full items-center pb-2 dark:border-gray-600"}
           [:button {:class (str "dark:text-gray-600 w-5 h-5 p-2 " (if open? "i-tabler-chevron-down" "i-tabler-chevron-right"))
                     :onClick #(setOpen (not open?))}]
           [:h1 {:class "dark:border-gray-600 border-gray-200 w-full border-b"} "tx: " (name)]]
          [Show {:when (fn [] open?)}
           [:div {:class "flex flex-col gap-3"}
            [Show {:when #(not (empty? (inputs)))}
             [:h2 {:class "font-bold"} (str "Inputs: ")]
             [Index {:each (inputs)}
              (fn [entry _]
                #jsx [ein/input entry #_{:readonly false
                                         :on-change (fn [e]

                                                      (ein/set-abi-field ctx [:function/id (:function/id (data)) :inputs i :value] e.target.value))}])]]
            (js/console.log "out " (outputs))
            [Show {:when #(not (empty? (outputs)))}
             [:h2 {:class "font-bold"} (str "Outputs: ")]
             [Index {:each (outputs)}
              (fn [entry i]
                #jsx [ein/input entry])]]]]]))

(def ui-function (comp/comp-factory Function AppContext))
