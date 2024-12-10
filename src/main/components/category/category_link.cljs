(ns main.components.category.category-link
  (:require ["./category.cljs" :as c]
            ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defc CategoryLink [this {:category-link/keys [id child]}]
  #jsx [:div {}
        [c/Category {:& (merge {:ident (child)
                                :link (id)} (dissoc props :ident))}]])
