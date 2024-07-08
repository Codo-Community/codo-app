(ns main.components.category.category-link
  (:require ["./category.cljs" :as c]
            ["../../comp.cljs" :as comp]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc CategoryLink [this {:category-link/keys [id child]}]
  #jsx [:div {}
        [c/ui-category {:& (merge {:ident (child)
                                   :link (id)} (dissoc props :ident))}]])

(def ui-category-link (comp/comp-factory CategoryLink AppContext))
