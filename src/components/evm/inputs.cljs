(ns co-who.components.evm.inputs
  (:require
            ["blo" :refer [blo]]
            ["../../evm/util.mjs" :as eu]
            ["../blueprint/input.jsx" :as i]
            ))

(defn address-input [{:keys [name value] :as data :or {name "Address"
                                                       value "0x0"}}
                     {:local/keys [on-change editable?]}]
  #jsx [:span {:class "gap-3 flex flex-row w-full items-center"}
        (i/input {:label (str name)
                  :left-icon (fn [] #jsx [:img {:class "rounded-md"
                                                :src (blo value)}])
                  :place-holder "0x..."
                  :readonly? (not editable?)
                  :copy? true
                  :on-change on-change} value)])

(defn number-input [{:keys [name value] :or {name "Value" value 0}}
                    {:local/keys [on-change editable?]}]
  (i/number-input {:label name
                   :placeholder 0
                   :readonly? (not editable?)
                   :on-change on-change} value))

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

(defn input [{:keys [internalType type name value] :as entry}
             {:local/keys [on-change editable?] :as local}]
  (condp = type
    "address" (address-input entry local)
    "uint256" (number-input entry local)
    "uint8" (number-input entry local)
    "uint48" (number-input entry local)
    "" #_(str entry)))
