(ns components.alert
  (:require ["solid-js" :refer [createSignal createMemo]]
            ["../comp.cljs" :as comp]
            ["../Context.cljs" :refer [AppContext]]
            ["../transact.cljs" :as t]
            ["./blueprint/alert.cljs" :as alert]
            ["@solid-primitives/timer" :refer [makeTimer]])
  (:require-macros [comp :refer [defc]]))

(defn alert-error [ctx]
  (fn [error]
    (println "error" error)
    (t/add! ctx {:component/id :alert
                 :title "Error"
                 :visible? true
                 :type :error
                 :interval 4000
                 :message (str error)})))

(defc Alert [this {:keys [component/id type message visible? interval] :as data :or {id :alert type :info message "" interval 3000}}]
  (let [timer (createMemo #(when (= (visible?) true)
                             (makeTimer (fn [] (t/set-field! ctx (conj (:ident props) :visible?)  false {:check-session? false}))
                                        (interval) js/setTimeout)))]
    #jsx [Show {:when (visible?)}
          [:div {:class "fixed bottom-10 shadow-lg w-fit flex justify-center mx-4 z-50"}
           [alert/alert {:type (type) :message (message)}]]]))

(def ui-alert (comp/comp-factory Alert AppContext))
