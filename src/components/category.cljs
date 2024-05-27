(ns components.category
  (:require ["../comp.mjs" :as comp]
            ["./blueprint/input.jsx" :as in]
            ["../utils.mjs" :as utils]

            ["../transact.mjs" :as t]
            ["./blueprint/button.jsx" :as b]
            ["../composedb/client.mjs" :as cli]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn category-mutation [query]
  (str "mutation setCategory($i: SetCategoryInput!){
          setCategory(input: $i){
            document { id name }
 } }"))

(defn mutation-vars [{:category/keys [id name categories] :as data}]
  {:i {:content (utils/drop-false {:name name :categories categories})}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} ident ]
  (fn [e]
    (if e (.preventDefault e))
    (-> (.executeQuery (:compose @cli/client)
                       (category-mutation {:document [:category/id :category/name :category/categories]})
                       (mutation-vars (n/pull store ident [:category/id :category/name :category/categories])))
        (.then (fn [response] (js/console.log response))))))


(defc Category [this {:category/keys [id categories]}]
  #jsx [:div {}
        []
        [b/button {:title "Get Name"
                   :on-click ( ctx)}]])

(def ui-category (comp/comp-factory Category AppContext))
