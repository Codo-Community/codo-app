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

(defc CategoryModal [this {:category/keys [id name {creator [:ceramic-account/id]} description] :as data
                           :or {id "" description ""}}]
  #jsx [:div {:class "dark:bg-black p-4 border-1 border-zinc-400 rounded-lg"}
        [:form {:class "flex flex-col gap-3"
                :onSubmit (fn [e] (.preventDefault e) (c/add-category-remote ctx (data) (:parent props)))}
         [:span {:class "flex  w-full gap-3"}
          [in/input {:label "Name"
                     :placeholder "Project Name"
                     :readonly (not (comp/viewer? this (creator)))
                     :value name
                     :on-change #(comp/set! this :category/name %)}]]
         [ta/textarea {:title "Description"
                       :value description
                       :readonly (not (comp/viewer? this (creator)))
                       :on-change #(comp/set! this :category/description %)}]
         [:span {:class "flex w-full gap-3"}
          [Show {:when (comp/viewer? this (creator))}
           [b/button {:title "Submit"
                      :data-modal-hide "planner-modal"}]]
          [b/button {:title "Close"
                     :on-click #(.preventDefault %)
                     :data-modal-hide "planner-modal"}]]]])

(def ui-category-modal (comp/comp-factory CategoryModal AppContext))
