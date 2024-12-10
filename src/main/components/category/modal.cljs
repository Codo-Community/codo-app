(ns components.category
  (:require ["solid-js" :refer [Show createSignal onMount useContext]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./category.cljs" :as c]
            ["../blueprint/input.cljs" :as in]
            ["../blueprint/textarea.cljs" :as ta]
            ["../blueprint/button.cljs" :as b])
  (:require-macros [sqeave :refer [defc]]))

(defc CategoryModal [this {:category/keys [id name {creator [:ceramic-account/id]} description] :as data
                           :or {id "" description ""}}]
  #jsx [:div {:class "dark:bg-black p-4 border-1 border-zinc-400 rounded-lg"}
        [:form {:class "flex flex-col gap-3"
                :onSubmit (fn [e] (.preventDefault e) (c/add-category-remote ctx (data) (:parent props)))}
         [:span {:class "flex  w-full gap-3"}
          [in/input {:label "Name"
                     :placeholder "Project Name"
                     :readonly (not (sqeave/viewer? this (creator)))
                     :value name
                     :on-change #(sqeave/set! this :category/name %)}]]
         [ta/textarea {:title "Description"
                       :value description
                       :readonly (not (sqeave/viewer? this (creator)))
                       :on-change #(sqeave/set! this :category/description %)}]
         [:span {:class "flex w-full gap-3"}
          [Show {:when (sqeave/viewer? this (creator))}
           [b/button {:title "Submit"
                      :data-modal-hide "planner-modal"}]]
          [b/button {:title "Close"
                     :on-click #(.preventDefault %)
                     :data-modal-hide "planner-modal"}]]]])
