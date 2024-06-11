(ns main.main
  (:require ["solid-js" :refer [createSignal]]
            ["./components/header.cljs" :as h]))

(defn Main [props]
  (let [[dark? setDark] (createSignal false)]
    #jsx [:div {:class (str "flex h-screen w-screen min-w-96 flex-col overflow-hidden text-gray-900 font-mono dark:text-white bg-[#f3f4f6] dark:bg-[#101014] " (if (dark?) "dark"))}
          [h/ui-header {:& {:ident (fn [] [:component/id :header])
                            :dark? dark?
                            :dark-toggle #(setDark (not (dark?)))}}]
          [:div {:class "flex h-screen w-sceen overflow-auto dark:text-white bg-[#f3f4f6] dark:bg-black  justify-center"}
           props.children]]))

; dark bg? [#101014]
