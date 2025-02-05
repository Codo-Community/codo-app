(ns co-who.components.evm.function
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For createSignal]]
            ["./input.cljs" :as ein]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../blueprint/label.cljs" :as l])
  (:require-macros [sqeave :refer [defc]]))

(defn seq-contains? [coll target] (some #(= target %) coll))

(defn remove-array-suffix [s]
  (if (and (>= (count s) 2)
           (= "[]" (subs s (- (count s) 2))))
    (subs s 0 (- (count s) 2))
    false))

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
         ;"len-----------------:" (count (inputs)) (:name (nth (inputs) 1)) " " (:type (nth (inputs) 1))
         [For {:each (inputs)}
          (fn [entry i]
            #jsx [Show {:when (remove-array-suffix (:type entry))
                        :fallback (fn [] #jsx [ein/input {:& (conj entry {:value #(:value entry)
                                                                          :on-change #(ein/set-abi-field ctx [:function/id (second (this.ident)) :inputs (i) :value] (-> % :target :value))})}])}

                  #jsx [:div {}
                        [l/label {:title (:name entry)}]
                        [For {:each (:value entry)}
                         (fn [v j]
                           #jsx [ein/input {:& {:value (fn [] v)
                                                :on-change #(ein/set-abi-field ctx [:function/id (second (this.ident)) :inputs (i) :value (j)] (-> % :target :value))}}])]
                        [:button {:onClick #(let [stripped (remove-array-suffix (:type entry))
                                                  t->d {"bytes" []}]
                                              (println "str:" stripped " " (:type entry))
                                              (ein/set-abi-field ctx [:function/id (second (this.ident)) :inputs (i) :value] (conj (:value entry) "")))} "+"]]])]]
        [Show {:when (not (empty? (outputs)))}
         [:h2 {:class "font-bold"} (str "Outputs: ")]
         [For {:each (outputs)}
          (fn [entry i]
            #jsx [:div {}
                  [ein/input {:& (conj entry {:value #(:value entry)
                                              :readonly true})}]])]]])
