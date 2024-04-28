(ns co-who.components.evm.inputs
  (:require [mr-who.dom :as dom]
            [co-who.blueprint.blockie :as blo]
            [co-who.evm.util :as eu]
            [co-who.blueprint.input :as i]
            ))

(defn address-input [{:keys [name value] :as data :or {name "Address"
                                                       value "0x0"}}
                     {:local/keys [on-change editable?]}]
  (dom/span {:class "gap-3 flex flex-row w-full items-center"}
            (i/input {:label (str name)
                      :left-icon (dom/img {:class "rounded-md"
                                           :src [(first value) (second value) (blo/make-blockie (last value))]})
                      :place-holder "0x..."
                      :readonly? (not editable?)
                      :copy? true
                      :on-change on-change} value)))

(defn number-input [{:keys [name value] :or {name "Value" value 0}}
                    {:local/keys [on-change editable?]}]
  (i/number-input {:label name
                   :placeholder 0
                   :readonly? (not editable?)
                   :on-change on-change
                   } value))

(defn set-abi-field [path value & convert-fn]
  (swap! co-who.app/app assoc-in (conj path :value) value))


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
