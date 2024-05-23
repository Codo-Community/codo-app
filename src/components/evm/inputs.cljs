(ns co-who.components.evm.inputs
  (:require ["blo" :refer [blo]]
            ["../../evm/util.mjs" :as eu]
            ["../blueprint/input.jsx" :as i]
            ["../../comp.mjs" :as comp]
            ["../../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn address-input [{:keys [name value readonly on-change] :as data :or {label "Address"
                                                                          value #(or (:value (data)) "0x0")
                                                                          on-change (fn [])
                                                                          readonly false}}]
  (let [data (if data.children data.children data)
        ;{:keys [name value readonly on-change]} data
        ]
    (println "fa " (data))
    #jsx [:span {:class "gap-3 flex flex-row w-full items-center"}
          [i/input {:label (:name (data))
                    :left-icon (fn [value] #jsx [:img {:class "rounded-md"
                                                       :src (blo (if (fn? (value)) ((value)) (value)))}])
                    :value #(or (:value (data)) "0x0")
                    :place-holder "0x..."
                    :readonly readonly
                    :copy true
                    :on-change on-change}]]))
