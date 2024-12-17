(ns pages.home
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["@solidjs/router" :refer [useNavigate]]
            ["../components/blueprint/card.cljs" :as c]
            ["../components/blueprint/button.cljs" :as b])
  (:require-macros [sqeave :refer [defc]]))

(defc HomePage [this {:keys [pages/id activity]}]
  (let [navigate (useNavigate)]
    #jsx [:div {:class ""}
          [:div {:class "grid h-fit xl:grid-cols-3 xl:grid-rows-4 md:(grid-cols-2 grid-rows-3 h-full) grid-flow-row w-full dark:(text-white) p-3 gap-3 w-screen"}
           [c/card {:& {:title "Activity"
                        :icon "i-tabler-activity"}}
            [b/button {:icon "i-tabler-plus"
                        :extra-class ""
                        :on-click #(navigate "/projects")}]
            [:p {:class "text-zinc-400 ml-1"} "No recent activity."]]

           [c/card {:& {:title "Projects"
                        :icon "i-tabler-stack"}}
            [:div {:class "flex gap-2"}
             [:p {:class "text-zinc-400 ml-1"} "No projects joined, join or create one today!"]]]

           [c/card {:& {:title "Wizards"
                        :extra-class "md:row-start-2 row-span-2"
                        :icon "i-tabler-wand"}}
            [:div {:class "flex flex-col gap-2"}
             [:h2 {:class "text-bold text-xl"} "Project"]
             [b/button {:icon "i-tabler-plus"
                        :extra-class ""
                        :on-click #(navigate (str "/wizards/new-project/" (js/crypto.randomUUID)))}]

             [:h2 {:class "text-bold text-xl"} "Organization"]
             [b/button {:icon "i-tabler-plus"
                        :extra-class ""
                        :on-click #(navigate (str "/wizards/new-organization/" (js/crypto.randomUUID)))}]

             #_[:h2 {:class "text-bold text-xl"} "Verification"]
             #_[:button {:class "hover:ring-2 ring-blue-500 bg-white rounded-md w-fit h-fit"}
              [A {:href (str "/wizards/verification/user/" "xyz")}
               [:img {:src (js/URL. "/images/sumsub.png" import.meta.url)
                      :class "w-20 h-20"}]]]]]]]))
