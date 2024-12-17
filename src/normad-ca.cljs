(ns normad)

(defn get-ident [data]
  (if-let [ident-key (first (filter #(re-find #"/id$" %) (keys data)))]
    (let [data2 (get data ident-key)]
      (if (or (string? data2) (int? data2))
        [ident-key (get data ident-key)]))))

(defn ident? [data]
  (if (vector? data)
    (if (string? (first data))
      (and
       (re-find #"/id$" (first data))
       (= (count data) 2))
      false)
    false))

(defn traverse-and-transform [item setStore]
  (cond
    (vector? item) (mapv #(traverse-and-transform % setStore) item)
    (map? item)  (if-let [ident (get-ident item)]
                   (let [new-val (zipmap (keys item) (mapv #(traverse-and-transform % setStore) (vals item)))]
                     (setStore (first ident)
                               (fn [x]
                                 (println "set ident:" ident ": " new-val)
                                 (update x (second ident) merge (get x (second ident)) new-val)))
                     ident)
                   (zipmap (keys item) (mapv #(traverse-and-transform % setStore) (vals item))))
    :else item))

#_(defn normalize-store [store]
  (let [[store setStore] store
        res (traverse-and-transform store setStore)]
    (doall (for [[k v] res]
             (setStore k (fn [x] v))))
    [store setStore]))

(defn add [{:keys [store setStore] :as ctx} & data]
  (let [res (traverse-and-transform (or (first data) store) setStore)]
    (doall (for [[k v] res]
               (setStore k (fn [x] (println "end set store:" k ": " v) v))))
    #_(vec (map-indexed (fn [k v] (setStore k (fn [x] v))  res)))
    ctx))

(defn pull [store entity query]
  (cond
    (ident? entity) (pull store (get-in store entity) query)

    (and (> (count entity) 0)
         (vector? entity)) (mapv (fn [x] (if (ident? x)
                                           (pull store x query)
                                           x)) entity)

    (and (> (count query) 1)
         (vector? query)) (let [simple-keys (filterv string? query)
                                not-simple  (filterv #(not (string? %)) query)]
         (into (zipmap simple-keys (mapv #(pull store entity %) simple-keys))
               (mapv #(pull store entity %) not-simple)))

    (and (= (count query) 1)
         (vector? query)) (pull store entity (first query))

    (map? query) (let [nk (first (keys query))
                       data (get entity nk)]
                   {nk (pull store data (vals query))})

    :else (get entity query)))

(comment (let [[store setStore] (normalize-store (createStore {:counters [{:counter/id 0
                                                                           :counter/value 1}
                                                                          {:counter/id 1
                                                                           :counter/value 2}]
                                                               :header {:something "title"
                                                                        :user {:user/id 0
                                                                               :user/name "da"
                                                                               :user/ethereum-address "0x0"
                                                                               :user/leg {:leg/id "left"}}}}))
               query [{:counters [:counter/id :counter/value]} {:header [{:user [:user/id :user/name]}]}]]
           (pull store store [:counters]))

         (ident? [:asd/id 0]))

#_(defn traverse-and-transform [data acc]
    (cond
      (map? data)
      (if-let [ident (get-ident data)]
        ;; If there is an ident, update the accumulator and possibly continue processing
        (reduce-kv
         (fn [a k v] (traverse-and-transform v (assoc-in a ident data)))
         acc
         (dissoc data (first ident)))
        ;; If no ident, recursively process each value in the map, passing and updating the accumulator
        (reduce-kv
         (fn [a k v] (assoc a k (traverse-and-transform v a)))
         acc
         data))

      (vector? data)
      ;; If a vector, apply the function to each item, passing the current accumulator state
      (reduce
       (fn [a v] (traverse-and-transform v a))
       acc
       data)

      :else
      ;; If it's a leaf node (neither map nor vector), return the accumulator unchanged
      acc))

#_(defn traverse-and-transform [data]
    (cond
      (map? data) (if-let [ident (get-ident data)]
                    ident
                    (zipmap (keys data) (mapv traverse-and-transform (vals data))))
      (ident? data) data
      (vector? data) (mapv traverse-and-transform data)
      :else data))


#_(defn normalize [data]
  (loop [data {}
         acc {}]
    (recur (traverse-and-transform data acc))))

#_(defn extract-idents [item]
  "Extracts entity if it contains an :id key."
  (when (contains? item :id)
    [(:id item) item]))

#_(defn extract-entity [item]
  "Extracts entity if it contains an :id key."
  (when-let [id (:id item)]
    [id (dissoc item :id)]))

#_(defn normalize [data]
  (letfn [(recurse [item]
            (cond
              (map? item)
              (if-let [[id entity] (extract-entity item)]
                ;; If item is an entity with an ID, return a reference and add to entities.
                [[:id id] {id entity}]
                ;; Otherwise, process each key-value pair.
                (let [transformed (map (fn [[k v]] (let [[ref ent] (recurse v)]
                                                     [k ref] ent))
                                       (seq item))]
                  [(into {} (map first transformed))
                   (apply merge (map second transformed))]))
              (coll? item)
              (let [transformed (map recurse item)]
                [(mapv first transformed) (apply merge (map second transformed))])
              :else [item {}]))] ; Non-mappable items are returned as-is with no entities.
    (let [[refs entities] (recurse data)]
      {:refs refs :id entities})))

(def example-input {:counters [{:counter/id 0 :counter/value 1}]
                    :header {:user {:user/id 0 :user/ethereum-address "0x0"}}})


#_(defn get-ident [data]
  ;; Assuming ident is something like [:user/id 1]
  (if-let [ident-key (first (filter #(re-find #"/id$" %) (keys data)))]
    [ident-key (get data ident-key)]))

#_(defn get-ident [data]
  "Extracts the identifier if present in the map."
  (first (filter #(re-find #"/id$" %) (keys data))))

#_(defn traverse-and-transform [data acc]
  (cond
    (map? data)
    (if-let [ident-key (get-ident data)]
      ;; If an identifier key is present, update the accumulator and reference in place.
      (let [ident-val (get data ident-key)
            updated-data (assoc data ident-key [ident-key ident-val])
            new-acc (assoc-in acc [ident-key ident-val] (dissoc updated-data ident-key))]
        (reduce-kv
         (fn [a k v]
           (if (= k ident-key)
             [a updated-data]
             (let [[new-a new-v] (traverse-and-transform v a)]
               [new-a (assoc updated-data k new-v)])))
         [new-acc updated-data]
         data))
      ;; No identifier, just process normally
      (reduce-kv
       (fn [a k v]
         (let [[new-a new-v] (traverse-and-transform v a)]
           [new-a (assoc data k new-v)]))
       [acc data]
       data))
    (vector? data)
    ;; Process each element in the vector, updating the accumulator and data.
    (let [results (mapv #(traverse-and-transform % acc) data)]
      [(first (first results)) (mapv second results)])
    :else
    ;; Base case: return the accumulator and data unmodified.
    [acc data]))

;; Example usage:
(comment
  (second (traverse-and-transform
           {:user/id 1
            :name "Alice"
            :address {:address/id 2
                      :street "123 Elm St"
                      :city "Metropolis"}}
           {}))

         (traverse-and-transform [example-input []])

         (def data {:user {:id 1 :name "Alice" :address {:id 5 :street "123 Elm St"}}
                    :posts [{:id 2 :title "Hello World"}
                            {:id 3 :title "Normalization"}]})

         (def answer {:user [:id 1]
                      :posts [[:id 2] [:id 3]]
                      :id {1 {:id 1 :name "Alice" :address [:id 5]}
                           2 {:id 2 :title "Hello World"}
                           3 {:id 3 :title "Normalization"}
                           5 {:id 5 :street "123 Elm St"}}})

         (normalize data))
