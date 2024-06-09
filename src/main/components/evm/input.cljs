(ns components.evm.input
  (:require ["./inputs.jsx" :as i]
            ["../blueprint/input.jsx" :as in]))

(defn dummy []
  #jsx[:div])

(defn input [{:keys [type name] :as entry}]
  (condp = type
      "address" [i/address-input entry]
      "uint256" [in/number-input entry]
      "uint8" [in/number-input (conj entry {:label name})]
      "uint48" [in/number-input (conj entry {:label name})]
      "bool" [in/boolean-input (conj entry {:label name})]
      ""))



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
