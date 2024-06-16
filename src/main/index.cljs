(ns index
  (:require ["./App.cljs" :as app]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["@unocss/reset/tailwind.css"]
            ["virtual:uno.css"]))

(println "start")
(render  #jsx [app/ui-root] (js/document.getElementById "root"))

#_(when js/import.meta.hot
  (js/import.meta.hot.accept #(js/import.meta.hot.invalidate)))
