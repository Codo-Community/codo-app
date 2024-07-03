(ns components.alert
  (:require ["solid-js" :refer [createSignal createMemo]]
            ["../comp.cljs" :as comp]
            ["@solidjs/router" :refer [A]]
            ["../Context.cljs" :refer [AppContext]]
            ["./blueprint/alert.cljs" :as alert]
            ["@solid-primitives/timer" :refer [makeTimer]])
  (:require-macros [comp :refer [defc]]))

#_(:visible (local))

(defc Alert [this {:keys [component/id type message visible? interval] :as data :or {id :alert type :info message "" interval 3000}}]
  (let [timer (createMemo #(when (= (visible?) true)
                             (makeTimer (fn [] (comp/set! this (:ident props) :visible? false)) (interval) js/setTimeout)))]
    #jsx [Show {:when (visible?)}
          [:div {:class "fixed bottom-10 shadow-lg w-fit flex justify-center mx-4"}
           [alert/alert {:type (type) :message (message)}]]]))

(def ui-alert (comp/comp-factory Alert AppContext))
