(ns co-who.components.evm.inputs
  (:require ["blo" :refer [blo]]
            ["../blueprint/input.cljs" :as i]))

(defn address-input [{:keys [name value readonly on-change] :as data :or {label "Address"
                                                                          value (fn [] "0x0")
                                                                          on-change (fn [])
                                                                          readonly false}}]
  #jsx [:span {:class "gap-3 flex flex-row w-full items-center"}
        [i/input {:label name
                  :left-icon (fn [value] #jsx [:img {:class "rounded-md"
                                                     :src (blo (or (value) "0x0"))}])
                  :value value
                  :place-holder "0x..."
                  :readonly readonly
                  :copy true
                  :on-change on-change}]])
