(ns co-who.components.evm.inputs
  (:require [solid-js :refer [createMemo]]
            ["blo" :refer [blo]]
            ["../blueprint/input.cljs" :as i]))

(defn address-input [{:keys [name value readonly on-change] :as data :or {label "Address"
                                                                          value (fn [] "0x0")
                                                                          on-change (fn [])
                                                                          readonly false}}]
  (let [img (createMemo (fn [] (blo (or (value) "0x0"))))]
    #jsx [:span {:class "gap-3 flex flex-row w-full items-center"}
          [i/input {:label name
                    :left-icon (fn [value] #jsx [:img {:class "relative rounded-md top-[6px]"
                                                       :src (img)}])
                    :value value
                    :place-holder "0x..."
                    :readonly readonly
                    :copy true
                    :on-change on-change}]]))
