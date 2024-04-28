(ns co-who.components.evm.function
  (:require [mr-who.dom :as dom]
            [co-who.blueprint.button :as b]
            [co-who.components.evm.inputs :as ein]
            [co-blue.icons.chevron-right :refer [chevron-right]]
            [co-who.blueprint.label :as l]))

(defn function-comp [{:keys [function/id name inputs outputs stateMutability type] :as data}
                     {:local/keys [on-change open?] :or {open? true} :as local}]
  (dom/div {}
    (dom/span {:class "flex inline-flex w-full items-center pb-2 dark:border-gray-600 border-b-2"}
      (b/icon-button {:class "dark:text-white-600"} (chevron-right))
      (dom/h1 {:class "font-bold dark:border-gray-600 border-gray-200 "}
        (str  "Transaction: " name)))
    (dom/div {:class (if (not open?) "hidden " " ")}
      (map-indexed (fn [i entry]
                     (ein/input entry {:local/editable? true
                                   :local/on-change (fn [e]
                                                      (ein/set-abi-field [:function/id id :inputs i] e.target.value))}))
                   inputs)
      (if (> (count outputs) 0)
        (l/label (str "Outputs:") "text-red-700")
        (map-indexed (fn [i entry]
                     (ein/input entry {:local/editable? false}))
                     outputs)))))

(defn abi-entry [data & local]
  (condp = (:type data)
    "function" (function-comp data local)
    "constructor" ""
    "error" ""
    "event" ""
    :cljs.spec.alpha/invalid "inv"
    (str data)))
