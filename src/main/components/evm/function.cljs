(ns co-who.components.evm.function
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["../blueprint/button.cljs" :as b]
            ["./input.cljs" :as ein]
            ["../../normad.cljs" :as n :refer [add]]
            ["../blueprint/label.cljs" :as l]
            ["../../Context.cljs" :as c]
            ["../../comp.cljs" :as comp])
  (:require-macros [comp :refer [defc]]))
;
(defc Function [this {:keys [function/id name type stateMutability
                             {inputs [:internalType :type :name :value]}
                             {outputs [:internalType :type :name :value]}]}]
  #jsx [:div {:class "flex flex-col gap-3"}
        [:span {:class "flex inline-flex w-fit items-center dark:border-gray-600"}
         [:h2 {:class "dark:border-gray-600 border-gray-200 w-full text-md font-bold"} "Function: "]
         [:text {:class "font-normal"} (name)]]
        [:div {:class "flex flex-col gap-3"}
         [Show {:when (not (empty? (inputs)))}
          [:h3 {:class "font-bold text-sm"} (str "Inputs: ")]
          [For {:each (inputs)}
           (fn [entry i]
             #jsx [ein/input {:& (conj entry {:value #(:value entry)
                                              :on-change #(ein/set-abi-field ctx [:function/id (id) :inputs (i) :value] (-> % :target :value))})}])]]
         [Show {:when (not (empty? (outputs)))}
          [:h2 {:class "font-bold"} (str "Outputs: ")]
          [For {:each (outputs)}
           (fn [entry i]
             #jsx [ein/input {:& (conj entry {:value #(:value entry)
                                              :readonly true})}])]]]])

(def ui-function (comp/comp-factory Function c/AppContext))
