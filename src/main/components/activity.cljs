(ns main.components.activity
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["@solidjs/router" :refer [A]]
            ["./user.cljs" :as u :refer [ui-user]]
            ["../comp.cljs" :as comp]
            ["./blueprint/card.cljs" :as c]
            ["../transact.cljs" :as t]
            ["../utils.cljs" :as utils]
            ["./blueprint/button.cljs" :as b]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Activity [this {:keys []}]
  #jsx [:div {:class "grid xl:grid-cols-3 md:grid-cols-2 xl:grid-rows-4 grid-flow-row w-full h-full dark:text-white p-3 gap-3 w-screen"}
        [c/card {:& {:title "Activity"
                     :icon "i-tabler-activity"}}
         [:p {:class "text-zinc-600 dark:text-zinc-400 ml-1"} "No recent activity."]]

        [c/card {:& {:title "Activity"
                     :icon "i-tabler-activity"}}
         [:p {:class "text-zinc-600 dark:text-zinc-400 ml-1"} "No recent activity."]
        [:button {:class "text-gray-400 bg-transparent
                         hover:text-gray-900 rounded-md text-sm p-1.5 block h-10 w-10
                         ml-auto inline-flex items-center dark:hover:text-white "}
         [A {:class "flex items-center"
             :href (str "/wizards/new-project/" (js/crypto.randomUUID))}
          [:div {:class "i-tabler-plus"}]]]]]
        )

(def ui-activity (comp/comp-factory Activity AppContext))
