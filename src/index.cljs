(ns index
  (:require ["./App.jsx" :refer [App]]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["virtual:windi.css"]
            ["virtual:windi-devtools"]))

(render App (js/document.getElementById "root"))
