(ns comp
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount]]
            ["./normad.mjs" :as n]
            ["blo" :refer [blo]]
            [squint.core :refer [defclass]])
  (:require-macros [compm :refer [defc]]))

(defclass Comp
  (field -ctx)
  (field -query)
  (field -data)

  (constructor [this ctx]
               (set! -ctx ctx))

  Object
  (ctx [_] -ctx)
  (ident [_ id])
  (data [_] -data)
  #_(query [] -query)
  (render [this ident]))

(defclass User
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
  (fn [p]
    (let [c (comp-class. ctx)]
      (c.render p))))

#_(defn defc [name bindings body]
  (defc 'name [] body))
