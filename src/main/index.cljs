(ns index
  (:require ["./App.jsx" :refer [Root]]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["virtual:uno.css"]
            ;["virtual:windi-devtools"]
            ["./start.mjs" :as s]))

(s/start)
(render Root (js/document.getElementById "root"))
