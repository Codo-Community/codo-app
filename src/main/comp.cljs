(ns comp
  (:require ["solid-js" :as solid]
            ["./normad.cljs" :as n]
            ["./transact.cljs" :as t]
            ["./utils.cljs" :as u]
            [squint.core :refer [defclass]]
            #_["./comp_macro.mjs" :as m])
  #_(:require-macros [comp :refer [defc]]))

(defn set! [this field event]
  (let [ctx (solid/useContext this.ctx)]
    (t/set-field! ctx (conj (this.ident) field) (u/e->v event))))

(defclass Comp
  (field ctx)
  (^:static field query)
  (field -data)
  (field ident)

  (constructor [this ctx-in]
               (set! ctx ctx-in))

  Object
  (^:static get-query [_] query)
  #_(ctx [_] -ctx)
  #_(ident [_ id] -ident)
  (data [_] -data)
  (-query [this] this.query)
  (render [this ident]))

#_(defclass User
  (extends Comp)
  (field -query [:user/id :user/ethereum-address])
  (field -local)
  (field -data)

  (constructor [_ ctx]
               (super ctx))

  Object
  (ident [_ id] (-ident id))

  (render [this ident]
          (let [{:keys [store setStore]} (useContext (this.ctx))
                data (createMemo #(n/pull store ident this.-query))
                {:user/keys [id ethereum-address]} {:user/id #(:user/id (data))
                                                    :user/ethereum-address #(:user/ethereum-address (data))}]
            #jsx [:div {:class "flex flex-inline  items-center justify-items-center text-white"}
                  [:img {:class "rounded-lg w-11 h-11 rounded-p md-0.5 text-gray-500 hover:bg-gray-100
                                 focus:outline-none focus:ring-4 focus:ring-gray-200 dark:text-gray-400
                                 dark:hover:bg-gray-700 dark:focus:ring-gray-700 select-none"
                         :draggable false
                         :onDragStart nil
                         :src (blo (ethereum-address))}]])))

(defn comp-factory [comp-class ctx]
  (let [c (new comp-class ctx)]
    #(c.render %)))

#_(defn defsc [name bindings body]
  (defc name bindings body))

(def default Comp)
(def useContext solid/useContext)
(def pull n/pull)
(def createMemo solid/createMemo)
(def ident? n/ident?)
#_(def defc m/defc)
