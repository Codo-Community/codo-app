(ns components.evm.input
  (:require ["./inputs.cljs" :as i]
            ["../../evm/util.cljs" :as eu]
            ["../blueprint/input.cljs" :as in]))

(defn input [{:keys [type name] :as entry}]
  (condp = type
    "address" #jsx [i/address-input {:& entry}]
    "uint256" #jsx [in/number-input {:& (conj entry {:label name})}]
    "uint8" #jsx [in/number-input {:& (conj entry {:label name})}]
    "uint48" #jsx [in/number-input {:& (conj entry {:label name})}]
    "bool" #jsx [in/boolean-input {:& (conj entry {:label name})}]
    ""))

(defn set-abi-field [{:keys [store setStore] :as ctx} path value & convert-fn]
  (println path)
  (setStore (first path)
            (fn [x]
              (assoc-in x (rest path) value))))

(defn convert-input-filter [input]
  (condp = (:type input)
    "address" (:value input)
    "uint256" (eu/parse-ether (:value input))
    "uint8" (eu/parse-ether (:value input))
    "uint48" (eu/parse-ether (:value input))))
