(ns utils
  (:require [squint.string :as string]
            ["lodash" :refer [camelCase startCase kebabCase]]))

(defn ident?
  "Check if x is a EQL ident."
  [x]
  (and (vector? x)
       (string? (first x))
       (= 2 (count x))
       (or (string? (second x)) (number? (second x)) (undefined? (second x)))))

(defn string? [thing]
  (= (js/typeof thing) "string"))

(defn uuid? [s]
  (re-matches #"^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$" s))

(defn stream-id? [s]
  (re-matches #"^kjz[a-zA-Z0-9]{43,}$" s))

(defn remove-item [v item]
  (vec (filter #(not= % item) v)))

(defn uuid [] (js/crypto.randomUUID))

(def camel-case camelCase)
(def kebab-case kebabCase)

(defn pascal-case [s]
  (startCase (camelCase s)))

(defn e->v [e]
  (-> e :target :value))

(defn remove-ns [thing]
  (cond (vector? thing) (mapv remove-ns thing)
        (string? thing) (or (second (string/split thing "/")) thing)
        (map? thing) (zipmap (mapv remove-ns (keys thing)) (remove-ns (vals thing)))))

(defn copy-to-clipboard [text-to-copy]
  (.writeText (.-clipboard js/navigator) text-to-copy))

(defn random-evm []
  (string/join "0x" (.toString (js/crypto.randomBytes 32) "hex")))

(defn drop-false [m]
  (into {} (filterv (fn [x] (and (not (false? (second x))) (not (nil? (second x))))) m)))

(defn distribute [f m]
  (cond (map? m) (zipmap (keys m) (mapv f (vals m)))
        (vector? m) (mapv f m)
        :else m))

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
