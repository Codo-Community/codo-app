(ns components.alert
  (:require ["solid-js" :refer [createSignal]]
            ["./comp.cljs" :as comp]
            ["@solidjs/router" :refer [A]]
            ["./Context.cljs" :refer [AppContext]]
            ["./blueprint/alert.cljs" :as alert])
  (:require-macros [comp :refer [defc]]))


(defc Alert [this {:component/keys [id type message] :as data :or {id type message}}]
  (let []
    #jsx [Show {:when true#_(:visible (local))}
          [:div {:class "fixed bottom-10 left-10 p-4 bg-white dark:bg-black shadow-lg rounded-lg"}
           [alert/alert {:type type :message message}]]]))

(def ui-alert (comp/comp-factory Alert AppContext))
