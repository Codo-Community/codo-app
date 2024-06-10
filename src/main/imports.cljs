(ns imports
  (:require ["solid-js" :refer [lazy]]))

(def WizardNewProject (lazy (fn [] (js/import "./components/wizards/new_project/main.cljs"))))
