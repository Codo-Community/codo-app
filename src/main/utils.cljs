(ns utils)

(defn ident?
  "Check if x is a EQL ident."
  [x]
  (and (vector? x)
       (keyword? (first x))
       (= 2 (count x))))

(defn string? [thing]
  (= (js/typeof thing) "string"))

(defn uuid? [s]
  (re-matches #"^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$" s))

(defn entity-key [k]
  (str (name k) "s"))

(defn normalize-entity [entity acc]
  (reduce-kv (fn [a k v]
               (cond
                 (ident? k) (-> a
                                (update-in [entity-type k] merge {v (assoc entity k v)})
                                (assoc entity k [entity-type v]))
                 (vector? v) (mapv #(normalize-entity % a) v)
                 (map? v) (normalize-entity v a)
                 :else (do (println a) a)))
             acc entity))

(defn normalize [data]
  (reduce (fn [acc item]
            (normalize-entity item acc))
          {} data))

(defn pull [db query]
  (if (keyword? query)
    {query (get db query)}
    (let [[entity-key query-vals] query]
      (reduce (fn [result [key nested-query]]
                (if (keyword? nested-query)
                  (assoc result nested-query (get-in db [entity-key key nested-query]))
                  (assoc result key (pull db [key nested-query]))))
              {}
              query-vals))))

(defn copy-to-clipboard [text-to-copy]
  (.writeText (.-clipboard js/navigator) text-to-copy))

(defn random-evm []
  (string/join "0x" (.toString (js/crypto.randomBytes 32) "hex")))

(defn drop-false [m]
  (into {} (filterv (fn [x] (and (not (false? (second x))) (not (nil? (second x))))) m)))

(defn nsd [data ns]
  (zipmap (mapv #(str ns "/" %) (keys data)) (vals data))
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
