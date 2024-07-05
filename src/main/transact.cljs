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

(declare check-session)
(declare alert-error)

(defn add! [{:keys [store setStore] :as ctx} value {:keys [append replace after check-session?] :or {append false replace false after
                                                                                                     false check-session? false} :as params}]
  (try
    (if check-session?
      (check-session ctx))
    (let [res (n/add ctx value)]
      (if (or append replace)
        (add-ident! ctx res params))
      (if after
        (after)))
    (catch js/Error e
      (alert-error ctx e)
      (println e))))

(defn alert-error [ctx error]
  (add! ctx {:component/id :alert
             :title "Error"
             :visible? true
             :type :error
             :interval 4000
             :message (str error)} {}))

(defn check-session [{:keys [store setStore] :as ctx}]
  (println "cn: " (n/pull store [:viewer/id 0] [{:ceramic-account/id [:ceramic-account/session]}]))
  (if-not (n/pull store  [:viewer] [{:ceramic-account/id [:ceramic-account/session]}])
    (throw (js/Error. "Sign in to to make changes."))))

(defn remove-entity! [])
