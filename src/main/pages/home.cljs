(ns pages.home
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../normad.cljs" :as n]
            ["@solidjs/router" :refer [A]]
            ["@solidjs/router" :refer [useNavigate useParams]]
            ["../components/activity.cljs" :as a]
            ["../components/blueprint/card.cljs" :as c]
            ["../components/blueprint/button.cljs" :as b]
            ["../utils.cljs" :as u]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn HomePage [props]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        navigate (useNavigate)
        data (createMemo #(n/pull store [:pages/id :home] [:activity]))]
    #jsx [:div {:class ""}
          [:div {:class "grid h-fit xl:grid-cols-3 xl:grid-rows-4 md:(grid-cols-2 grid-rows-3 h-full) grid-flow-row w-full dark:(text-white) p-3 gap-3 w-screen"}
           [c/card {:& {:title "Activity"
                        :icon "i-tabler-activity"}}
            [:p {:class "text-zinc-700 dark:text-zinc-400 ml-1"} "No recent activity."]]

           [c/card {:& {:title "Projects"
                        :icon "i-tabler-stack"}}
            [:div {:class "flex gap-2"}
             [:p {:class "text-zinc-700 dark:text-zinc-400 ml-1"} "No projects joined, join or create one today!"]]]

           [c/card {:& {:title "Wizards"
                        :extra-class "md:row-start-2 row-span-2"
                        :icon "i-tabler-wand"}}
            [:div {:class "flex flex-col gap-2"}
             [:h2 {:class "text-bold text-xl"} "Project"]
             [b/button {:icon "i-tabler-plus"
                        :extra-class ""
                        :on-click #(navigate (str "/wizards/new-project/" (js/crypto.randomUUID)))}]
             #_[:h2 {:class "text-bold text-xl"} "Verification"]
             #_[:button {:class "hover:ring-2 ring-blue-500 bg-white rounded-md w-fit h-fit"}
              [A {:href (str "/wizards/verification/user/" "xyz")}
               [:img {:src (js/URL. "/images/sumsub.png" import.meta.url)
                      :class "w-20 h-20"}]]]]]]]))
