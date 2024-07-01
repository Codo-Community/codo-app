(ns components.category
  (:require ["solid-js" :refer [Show createSignal onMount useContext]]
            ["@solid-primitives/active-element" :refer [createFocusSignal]]
            ["../../comp.cljs" :as comp]
            ["./category.cljs" :as c]
            ["../blueprint/input.cljs" :as in]
            ["../blueprint/textarea.cljs" :as ta]
            ["../../composedb/util.cljs" :as cu]
            ["../../utils.cljs" :as u]
            ["flowbite" :as fb]
            ["./query.cljs" :as cq]
            ["./menu.cljs" :as cm]
            ["@solidjs/router" :refer [cache createAsync]]
            ["../../transact.cljs" :as t]
            ["../blueprint/button.cljs" :as b]
            ["../../composedb/client.cljs" :as cli]
            ["../../normad.cljs" :as normad]
            ["../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc CategoryModal [this {:category/keys [id name description] :as data :or {id description}}]
  #jsx [:form {:class "flex flex-col gap-3"
               :onSubmit (fn [e] (.preventDefault e) (c/add-category-remote ctx (data) nil))}
        [:span {:class "flex  w-full gap-3"}
         [in/input {:label "Name"
                    :placeholder "Project Name"
                    :value name
                    :on-change #(comp/set! this :category/name %)}]]
        [ta/textarea {:title "Description"
                      :value description
                      :on-change #(comp/set! this :category/description %)}]
        [:span {:class "flex w-full gap-3"}
         [b/button {:title "Submit"}]]])

(def ui-category-modal (comp/comp-factory CategoryModal AppContext))
