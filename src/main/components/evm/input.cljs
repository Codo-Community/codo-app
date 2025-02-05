(ns components.evm.input
  (:require ["./inputs.cljs" :as i]
            ["../../evm/util.cljs" :as eu]
            ["../blueprint/input.cljs" :as in]))

(defn input [{:keys [type name] :as entry}]
  (condp = type
    "address" #jsx [i/address-input {:& (conj entry {:label name})}]
    "uint256" #jsx [in/number-input {:& (conj entry {:label name})}]
    "uint8" #jsx [in/number-input {:& (conj entry {:label name})}]
    "uint48" #jsx [in/number-input {:& (conj entry {:label name})}]
    "bool" #jsx [in/boolean-input {:& (conj entry {:label name})}]
    "string" #jsx [in/input {:& (conj entry {:label name})}]
    "bytes32" #jsx [in/input {:& (conj entry {:label name})}]
    #jsx [in/input {:& entry}] ; assume entry is a scalar
    ))

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
    "uint48" (eu/parse-ether (:value input))
    (:value input)))
