(ns co-who.components.evm.inputs
  (:require ["blo" :refer [blo]]
            ["../../evm/util.mjs" :as eu]
            ["../blueprint/input.jsx" :as i]
            ["../../comp.mjs" :as comp]
            ["../../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn address-input [{:keys [name value readonly on-change] :as data :or {label "Address"
                                                                          value (fn [] "0x0")
                                                                          on-change (fn [])
                                                                          readonly false}}]
  (println on-change)
  #jsx [:span {:class "gap-3 flex flex-row w-full items-center"}
        [i/input {:label name
                  :left-icon (fn [value] #jsx [:img {:class "rounded-md"
                                                     :src (blo (value))}])
                  :value value
                  :place-holder "0x..."
                  :readonly readonly
                  :copy true
                  :on-change on-change}]])
