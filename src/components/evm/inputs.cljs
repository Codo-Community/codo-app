(ns co-who.components.evm.inputs
  (:require ["blo" :refer [blo]]
            ["../../evm/util.mjs" :as eu]
            ["../blueprint/input.jsx" :as i]))

(defn address-input [{:keys [name value readonly on-change] :as data :or {name "Address"
                                                                          value "0x0"
                                                                          on-change (fn [])
                                                                          readonly false}}]
  #jsx [:span {:class "gap-3 flex flex-row w-full items-center"}
        [i/input {:label (str name)
                  :left-icon (fn [] #jsx [:img {:class "rounded-md"
                                                :src (blo (value))}])
                  :value value
                  :place-holder "0x..."
                  :readonly readonly
                  :copy true
                  :on-change on-change
                  }]])

(defn number-input [{:keys [name value on-change readonly] :or {name "Value" value 0 readonly false}}]
  [i/number-input {:label name
                   :placeholder 0
                   :readonly readonly
                   :value value
                   :on-change on-change}])

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

(defn input [{:keys [internalType type name value on-change readonly] :as entry}]
  (condp = type
    "address" #jsx [address-input entry]
    "uint256" #jsx [number-input entry]
    "uint8" #jsx [number-input entry]
    "uint48" #jsx [number-input entry]
    ""))
