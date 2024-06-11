(ns comp
  (:require ["solid-js" :as solid]
            ["./normad.cljs" :as n]
            ["./transact.cljs" :as t]
            ["./utils.cljs" :as u]
            [squint.core :refer [defclass]]
            #_["./comp_macro.mjs" :as m])
  #_(:require-macros [comp :refer [defc]]))

(def remotes (atom {}))

(defn set!
  ([ctx ident field event]
   (t/set-field! ctx (conj ident field) (u/e->v event)))
  ([this field event]
   (t/set-field! this.-ctx (conj (this.ident) field) (u/e->v event))))

(defclass Comp
  (field ctx)
  (^:static field query)
  (field mew-data)
  (field -data)
  (field ident)

  (constructor [this ctx-in]
               (set! ctx ctx-in))

  Object
  (^:static get-query [_] 1)

  #_(^:static new [_ & data] (println "a"))
  #_(ctx [_] -ctx)
  #_(ident [_ id] -ident)
  (data [_] -data)
  (-query [this] this.query)
  #_(-new [this] this.new)
  (render [this ident]))

(defn comp-factory [comp-class ctx]
  (let [c (new comp-class ctx)]
    (println "new factory: " c.render)
    #(c.render %)))

(defn new-data [this]
  (this.new-data))

(defn mutate! [this mutate-map]
  (let [local (:local mutate-map)
        add (or (:add local) (:add mutate-map))
        opts {:append (:append (or local mutate-map))
              :replace (:replace (or local mutate-map))}]
    (when add
      (t/add! this.-ctx (if (= add :new) (this.new-data) add) opts))))

(def default Comp)
(def useContext solid/useContext)
(def pull n/pull)
(def createMemo solid/createMemo)
(def ident? n/ident?)
