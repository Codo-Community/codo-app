(ns Context
  (:require ["solid-js" :refer [createContext]]))

(def AppContext (createContext))

(def default AppContext)
