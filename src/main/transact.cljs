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
          action (if append
                   #(update-in % (vec (rest path)) conj ident)
                   #(assoc-in % (vec (rest path)) ident))]
      (setStore (first path) (fn [x] (action x))))))

(defn remove-ident! [{:keys [store setStore] :as ctx} path ident]
  (setStore (first path) (fn [x] (update-in x (rest path) (fn [a]
                                                      (let [v (filterv (fn [y] (not (= (second y)
                                                                                       (second ident)))) a)]
                                                        v))))))

(defn add! [{:keys [store setStore] :as ctx} value {:keys [append replace] :or {append false replace false} :as params}]
  (let [res (n/add ctx value)]
    (if (or append replace)
      (add-ident! ctx res params))))

(defn remove-entity! [])
