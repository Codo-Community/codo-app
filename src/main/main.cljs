(ns main.main
  (:require ["solid-js" :refer [createSignal Suspense]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["solid-spinner" :as spinner]
            ["./components/alert.cljs" :as alert]
            ["./components/header.cljs" :as h]
            ["./components/footer.cljs" :as f])
  (:require-macros [sqeave :refer [defc]]))

(defc Main [this {:keys []}]
  (let [[dark? setDark] (createSignal true)]
    #jsx [:div {:class (str "flex h-screen w-screen flex-col overflow-hidden text-gray-900 font-mono bg-[#f3f4f6] dark:(text-white bg-black) justify-center " (if (dark?) "dark"))}
          [h/Header {:& {:ident [:component/id :header]
                            :dark? dark?
                            :dark-toggle #(setDark (not (dark?)))}}]

          [:div {:class "flex h-[calc(100vh-72px-56px)] w-screen dark:(text-white bg-black) justify-center"}
           #_[alert/Alert {:& {:ident [:component/id :alert]}}]

           [Suspense {:fallback (fn [] #jsx [spinner/TailSpin])}
            props.children]

           #_[:div {:class "absolute lg:right-6 top-20 flex lg:flex-col gap-2 lt-lg:bottom-1"}
              [:span {:class "flex gap-2 items-center justify-end flex"} [A {:href "/users"} "Users"] [:div {:class "i-tabler-user"}]]
              [:span {:class "flex gap-2 items-center justify-end flex"} [A {:href "/projects"} "Projects"] [:div {:class "i-tabler-stack"}]]]]

          [f/Footer]]))

                                        ; dark bg? [#101014]
