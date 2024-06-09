(ns transact
  (:require ["./normad.mjs" :as n]))

(defn set-field! [{:keys [store setStore] :as ctx} path value & convert-fn]
  (setStore (first path)
            (fn [x]
              (assoc-in x (rest path) value))))

(defn add-ident! [{:keys [store setStore] :as ctx} ident {:keys [append replace] :or {append false replace []} :as params}]
  (if (or append replace)
    (let [path (or append replace)
          action (if append #(update-in % (vec (rest path)) conj ident) #(assoc-in % (vec (rest path)) ident))]
      (setStore (first path) action))))

(defn remove-ident! [{:keys [store setStore] :as ctx} path ident]
  (setStore (first path) (fn [x]
                           (update-in x (vec (rest path)) #(filterv (fn [x] (not (= (second x)
                                                                                    (second ident)))) %)))))

(defn add! [{:keys [store setStore] :as ctx} value {:keys [append replace] :or {append false replace false} :as params}]
  (n/add ctx value)
  (println store)
  (if (or append replace)
    (add-ident! ctx (n/get-ident value) params)))

(defn remove-entity! [])
