(ns index
  (:require ["./App.cljs" :refer [Root]]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["virtual:uno.css"]
            ;["virtual:windi-devtools"]
            ))

(render #jsx [Root] (js/document.getElementById "root"))
