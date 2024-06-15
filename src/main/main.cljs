(ns main.main
  (:require ["solid-js" :refer [createSignal]]
            ["./comp.cljs" :as comp]
            ["./Context.cljs" :refer [AppContext]]
            ["./components/header.cljs" :as h])
  (:require-macros [comp :refer [defc]]))

(defc Main [this {:keys []}]
  (let [[dark? setDark] (createSignal true)]
    #jsx [:div {:class (str "flex h-screen w-screen min-w-96 flex-col overflow-hidden text-gray-900 font-mono dark:text-white bg-[#f3f4f6] dark:bg-[#101014] " (if (dark?) "dark"))}
          [h/ui-header {:& {:ident (fn [] [:component/id :header])
                            :dark? dark?
                            :dark-toggle #(setDark (not (dark?)))}}]
          [:div {:class "flex h-screen w-sceen overflow-auto dark:text-white bg-[#f3f4f6] dark:bg-black justify-center"}
           props.children]]))

(def ui-main (comp/comp-factory Main AppContext))

                                        ; dark bg? [#101014]
