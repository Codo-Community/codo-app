(ns index
  (:require ["./App.jsx" :refer [Root]]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["virtual:windi.css"]
            ["virtual:windi-devtools"]))

(render Root (js/document.getElementById "root"))
