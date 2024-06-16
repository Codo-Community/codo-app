(ns main.components.activity
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["@solidjs/router" :refer [A]]
            ["./user.cljs" :as u :refer [ui-user]]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["../utils.cljs" :as utils]
            ["./blueprint/button.cljs" :as b]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Activity [this {:keys []}]
  #jsx [:div {:class "grid grid-cols-4 grid-rows-4 grid-flow-row w-fit dark:text-white p-3 gap-3 w-screen"}

        [:div {:class "border border-2 border-zinc-800 p-3 rounded-lg select-none"}
         [:h1 {:class "font-bold text-xl"} "Activity"]
         [:div {:class "text-zinc-400"} "No recent activity."]]

        [:div {:class "border border-2 border-zinc-800 p-3 rounded-lg select-none"}
         [:span {:class "flex items-center "}
          [:h1 {:class "font-bold text-xl"} "Projects"]]
         [:button {:class "text-gray-400 bg-transparent
                         hover:text-gray-900 rounded-md text-sm p-1.5 block h-10 w-10
                         ml-auto inline-flex items-center dark:hover:text-white "}
          [A {:class "flex items-center"
              :href (str "/wizards/new-project/" (js/crypto.randomUUID))}
           [:div {:class "i-tabler-plus"}]]]]])

(def ui-activity (comp/comp-factory Activity AppContext))
