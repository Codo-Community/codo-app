(ns utils
  (:require [squint.string :as string]))

(defn ident?
  "Check if x is a EQL ident."
  [x]
  (and (vector? x)
       (string? (first x))
       (= 2 (count x))
       (or (string? (second x)) (number? (second x)))))

(defn string? [thing]
  (= (js/typeof thing) "string"))

(defn uuid? [s]
  (re-matches #"^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$" s))

(defn remove-ns [thing]
  (mapv #(if (string? %)
           (second (.split % "/"))
           (if (map? %) (zipmap (mapv (fn [k] (second (.split k "/"))) (keys %)) (vals %)))) thing))

(defn copy-to-clipboard [text-to-copy]
  (.writeText (.-clipboard js/navigator) text-to-copy))

(defn random-evm []
  (string/join "0x" (.toString (js/crypto.randomBytes 32) "hex")))

(defn drop-false [m]
  (into {} (filterv (fn [x] (and (not (false? (second x))) (not (nil? (second x))))) m)))

(defn nsd [data ns]
  (zipmap (mapv (fn [x] (str ns "/" x)) (keys data)) (vals data))
  #_(into {} (mapv (fn [[k v]] [(str ns "/" k) v]) data)))

;; local storage

(defn set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) key val))

(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) key))

(defn remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) key))
