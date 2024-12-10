(ns components.project
  (:require ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["../utils.cljs" :as utils]
            ["../transact.cljs" :as t]
            ["./blueprint/button.cljs" :as b]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [sqeave :refer [defc]]))

(defc ProjectPage [this {:keys [pages/id project]}]
  #_(onMount ((on-click-mutation ctx (data)) nil))
  #jsx [up/Project (project)])
