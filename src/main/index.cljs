(ns index
  (:require ["./App.jsx" :refer [Root]]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["flowbite" :as fb]
            ["virtual:windi.css"]
            ["virtual:windi-devtools"]))

(fb/initFlowbite)
(render Root (js/document.getElementById "root"))
