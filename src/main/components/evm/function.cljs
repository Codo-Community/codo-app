(ns co-who.components.evm.function
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["./input.cljs" :as ein]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))
;
(defc Function [this {:keys [function/id name type stateMutability
                             {inputs [:internalType :type :name :value]}
                             {outputs [:internalType :type :name :value]}]
                      :or {function/id (sqeave/uuid) name "SomeFun" type :none stateMutabiity false inputs [] outputs []}}]
  #jsx [:div {:class "flex flex-col gap-2"}
        [:span {:class "flex inline-flex w-fit items-center dark:border-gray-600"}
         [:h2 {:class "dark:border-gray-600 border-gray-200 w-full text-md font-bold"} "Function: "]
         [:text {:class "font-normal ml-2"} (name)]]
        [Show {:when (not (empty? (inputs)))}
         [:h3 {:class "font-bold text-sm"} (str "Inputs: ")]
         [For {:each (inputs)}
          (fn [entry i]
            #jsx [ein/input {:& (conj entry {:value #(:value entry)
                                             :on-change #(ein/set-abi-field ctx [:function/id (second (this.ident)) :inputs (i) :value] (-> % :target :value))})}])]]
        [Show {:when (not (empty? (outputs)))}
         [:h2 {:class "font-bold"} (str "Outputs: ")]
         [For {:each (outputs)}
          (fn [entry i]
            #jsx [ein/input {:& (conj entry {:value #(:value entry)
                                             :readonly true})}])]]])
