(ns main.components.activity
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["@solidjs/router" :refer [A]]
            ["./user.jsx" :as u :refer [ui-user]]
            ["../comp.mjs" :as comp]
            ["../transact.mjs" :as t]
            ["../utils.mjs" :as utils]
            ["./blueprint/button.jsx" :as b]
            ["solid-icons/hi" :refer [HiOutlinePlus]]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Activity [this {:keys []}]
  #jsx [:div {:class "grid w-screen dark:text-white p-3 gap-3"}

        [:div {:class "w-96 bg-zinc-800 p-3 rounded-lg select-none"}
         [:h1 {:class "font-bold text-xl"} "Activity"]
         [:div {} "No recent activity. Do something!"]]

        [:div {:class "w-96 bg-zinc-800 p-3 rounded-lg select-none"}
         [:h1 {:class "font-bold text-xl"} "Trending"]]

        [:div {:class "w-96 bg-zinc-800 p-3 rounded-lg select-none"}
         [:h1 {:class "font-bold text-xl"} "Actions"]]

        [:div {:class "w-96 bg-zinc-800 p-3 rounded-lg select-none"}
         [:span {:class "flex"}
          [:h1 {:class "font-bold text-xl"} "Projects"]
          [A {:href "/wizards/new-project"} [HiOutlinePlus]]
          ]]])

(def ui-activity (comp/comp-factory Activity AppContext))
