(ns main.components.category-tree
  (:require ["../comp.cljs" :as comp]
            ["./blueprint/input.cljs" :as in]
            ["../utils.cljs" :as utils]
            ["../composedb/util.cljs" :as cu]
            ["../utils.cljs" :as u]
            ["../transact.cljs" :as t]
            ["./blueprint/button.cljs" :as b]
            ["../composedb/client.cljs" :as cli]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc CategoryTree [this {:category/keys [id name children]}]
  #jsx [:div {}
        [b/button {:title "Add"
                   :on-click #(add-category ctx id)}]])

(def ui-category-tree (comp/comp-factory CategoryTree AppContext))
