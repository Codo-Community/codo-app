(ns components.evm.input
  (:require ["./inputs.jsx" :as i]
            ["../blueprint/input.jsx" :as in]))

(defn input [entry]
  (let [entry (if entry.children entry.children entry)
        {:keys [type name]} entry]
    (println "e " (:type (entry)))
    (condp = (:type (entry))
      "address" #jsx [i/address-input entry]
      "uint256" #jsx [in/number-input (conj entry {:label name})]
      "uint8" #jsx [in/number-input (conj entry {:label name})]
      "uint48" #jsx [in/number-input (conj entry {:label name})]
      "")))

(defn set-abi-field [{:keys [store setStore] :as ctx} path value & convert-fn]
  (setStore (first path)
            (fn [x]
              (assoc-in x (rest path) value))))

(defn convert-input-filter [input]
  (condp = (:type input)
    "address" (:value input)
    "uint256" (eu/parse-ether (:value input))
    "uint8" (eu/parse-ether (:value input))
    "uint48" (eu/parse-ether (:value input))))
