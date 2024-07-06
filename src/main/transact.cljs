(ns transact
  (:require ["./normad.cljs" :as n]))

#_(defn rec-add [path value]
  (loop [p (first path)
         r (rest path)]
    (into {} [p (if (nil? (first r))
                  value
                  (recur (first r) (rest r)))])))

(defn alert-error [ctx error]
  (n/add ctx {:component/id :alert
             :title "Error"
             :visible? true
             :type :error
             :interval 4000
             :message (str error)} {}))

(defn get-viewer-user [{:keys [store setStore] :as ctx}]
  (n/pull store [:viewer/id 0] [{:viewer/user [:user/id :user/session :user/account]}]))

(defn viewer-ident [ctx]
  (-> (get-viewer-user ctx) :viewer/user :user/account))

(defn viewer? [ctx acc]
  (let [viewer-id (second (viewer-ident ctx))]
    (and (= viewer-id acc) (not (nil? viewer-id)) (not (nil? acc)))))

(defn check-session [{:keys [store setStore] :as ctx}]
  (if-not (get-in (get-viewer-user ctx) [:viewer/user :user/session])
    (throw (js/Error. "Sign in to to make changes."))))

(defn wrap-session [ctx check-session? f]
  (try
    (if check-session?
      (check-session ctx))
    (f)
    (catch js/Error e
      (alert-error ctx e)
      (println e))))

(defn set-field! [{:keys [store setStore] :as ctx} path value {:keys [check-session?] :or {check-session? true}}]
  (wrap-session ctx check-session?
   #(setStore (first path)
             (fn [x]
               (assoc-in x (rest path) value)))))

(defn add-ident! [{:keys [store setStore] :as ctx} ident {:keys [append replace check-session?] :or {append false replace false check-session? true}}]
  (wrap-session ctx check-session?
                (fn []
                  (if (or append replace)
                    (let [path (or append replace)
                          action (if append
                                   #(update-in % (vec (rest path)) conj ident)
                                   #(assoc-in % (vec (rest path)) ident))]
                      (setStore (first path) (fn [x] (action x))))))))

(defn remove-ident! [{:keys [store setStore] :as ctx} path ident {:keys [check-session?] :or {check-session? true}}]
  (wrap-session ctx check-session?
                #(setStore (first path) (fn [x] (update-in x (rest path) (fn [a]
                                                                           (let [v (filterv (fn [y] (not (= (second y)
                                                                                                            (second ident)))) a)]
                                                                             v)))))))

(defn add! [{:keys [store setStore] :as ctx} value {:keys [append replace after check-session?] :or {append false replace false after
                                                                                                     false check-session? true} :as params}]
  (wrap-session ctx check-session?
                #(let [res (n/add ctx value)]
                   (if (or append replace)
                     (add-ident! ctx res params))
                   (if after
                     (after)))))

(defn remove-entity! [])

(defn swap-uuids! [ctx id stream-id]
  (n/swap-uuids! ctx id stream-id))
