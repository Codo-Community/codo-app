(ns index
  (:require ["./App.cljs" :as app]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["virtual:uno.css"]))

(render  #jsx [app/ui-root] (js/document.getElementById "root"))
