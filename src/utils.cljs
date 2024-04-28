(ns utils)

(defn ident?
  "Check if x is a EQL ident."
  [x]
  (and (vector? x)
       (keyword? (first x))
       (= 2 (count x))))

(defn string? [thing]
  (= (js/typeof thing) "string"))

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

#_(println "n: " (normalize {:counters {:counter/id 0
                                      :counter/count 1}}))
