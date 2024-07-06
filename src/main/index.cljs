(ns index
  (:require ["./app2.cljs" :as app]
            ["solid-js/web" :refer [render]]
            ["solid-devtools"]
            ["@unocss/reset/tailwind.css"]
            ["virtual:uno.css"]))

(println "start")

(defn ui-root []
  #jsx [:div {} "ava2222a22"])

(render (fn [] #jsx [app/ui-root]) (js/document.getElementById "root"))

#_(when js/import.meta.hot
    (js/import.meta.hot.accept #(js/import.meta.hot.invalidate)))
