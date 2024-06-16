(ns transact
  (:require ["./normad.cljs" :as n]))

#_(defn rec-add [path value]
  (loop [p (first path)
         r (rest path)]
    (into {} [p (if (nil? (first r))
                  value
                  (recur (first r) (rest r)))])))

(defn set-field! [{:keys [store setStore] :as ctx} path value & convert-fn]
  (setStore (first path)
            (fn [x]
              (assoc-in x (rest path) value)
              #_{(rest path) value})))

(defn add-ident! [{:keys [store setStore] :as ctx} ident {:keys [append replace] :or {append false replace []} :as params}]
  (if (or append replace)
    (let [path (or append replace)
          action (if append #(update-in % (vec (rest path)) conj ident) #(assoc-in % (vec (rest path)) ident))]
      (setStore (first path) (fn [x] (if x (action x) (if append [ident] ident)))))))

(defn remove-ident! [{:keys [store setStore] :as ctx} path ident]
  (setStore (first path) (fn [x]
                           #_(println "x " (get-in x (rest path)))
                           #_(println "ident " (second ident))

                           #_(println (mapv (fn [y]
                                            (not (= (second y)
                                                    (second ident)))) (get-in x (rest path))))

                           #_(println (filterv (fn [y] (not (= (second y)
                                                             (second ident)))) (get-in x (rest path))))

                           (update-in x (rest path) (fn [a]
                                                      (let [v (filterv (fn [y] (not (= (second y)
                                                                                       (second ident)))) a)]
                                                        v))))))

(defn add! [{:keys [store setStore] :as ctx} value {:keys [append replace] :or {append false replace false} :as params}]
  (n/add ctx value #_(merge {:data value} params))
  (if (or append replace)
    (add-ident! ctx (n/get-ident value) params)))

(defn remove-entity! [])
