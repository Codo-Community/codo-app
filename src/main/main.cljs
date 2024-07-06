(ns main.main
  (:require ["solid-js" :refer [createSignal]]
            ["./comp.cljs" :as comp]
            ["./Context.cljs" :refer [AppContext]]
            ["./components/alert.cljs" :as alert]
            ["./components/header.cljs" :as h]
            ["./components/footer.cljs" :as f])
  (:require-macros [comp :refer [defc]]))

(defc Main [this {:keys []}]
  (let [[dark? setDark] (createSignal true)]
    #jsx [:div {:class (str "flex h-screen w-screen flex-col overflow-hidden text-gray-900 font-mono dark:text-white bg-[#f3f4f6]  dark:bg-black " (if (dark?) "dark"))}
          [h/ui-header {:& {:ident [:component/id :header]
                            :dark? dark?
                            :dark-toggle #(setDark (not (dark?)))}}]
          [:div {:class "flex h-screen w-sceen dark:(text-white bg-black) justify-center"}
           [alert/ui-alert {:& {:ident [:component/id :alert]}}]
           
           props.children

           #_[:div {:class "absolute lg:right-6 top-20 flex lg:flex-col gap-2 lt-lg:bottom-1"}
              [:span {:class "flex gap-2 items-center justify-end flex"} [A {:href "/users"} "Users"] [:div {:class "i-tabler-user"}]]
              [:span {:class "flex gap-2 items-center justify-end flex"} [A {:href "/projects"} "Projects"] [:div {:class "i-tabler-stack"}]]]]

          [f/ui-footer]

          ]))

(def ui-main (comp/comp-factory Main AppContext))

                                        ; dark bg? [#101014]
