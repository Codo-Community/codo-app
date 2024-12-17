(ns main.components.organization
  (:require ["solid-js" :refer [For createSignal onMount createMemo useContext]]
            ["solid-js/web" :refer [Dynamic]]
            ["flowbite" :refer [initModals]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../composedb/util.cljs" :as cu]
            ["./blueprint/split.cljs" :as s]
            ["./blueprint/tabs.cljs" :as tabs]
            ["./blueprint/button.cljs" :as b]
            ["./blueprint/card.cljs" :as c]
            ["./blueprint/modal.cljs" :as modal])
  (:require-macros [sqeave :refer [defc]]))

(defc OrganizationReport [this {:organization/keys [id name start description
                                                    projects
                                                    {safe [:contract/id :contract/chain]}
                                                    {contracts [:contract/id :contract/chain]}] :or {id (sqeave/uuid)
                                                                                                     name "Proj"
                                                                                                     start "2021-01-01"
                                                                                                     description "Desc"
                                                                                                     projects []
                                                                                                     safe []
                                                                                                     contracts []}}]
  (let []
    (createMemo (fn [] (when (:modal (local)) (initModals))))
    #jsx [:div {:class "flex flex-col w-full items-center"}
          [c/card {}
           [:h1 {:class "text-xl"} (name)]]
          [:div {:class "grid h-fit xl:grid-cols-3 xl:grid-rows-4 md:(grid-cols-2 grid-rows-3 h-full) grid-flow-row w-full dark:(text-white) p-3 gap-3 w-screen"}
           [c/card {:& {:title "Projects"
                        :icon "i-tabler-activity"}}
            [Show {:when (not (empty? (projects))) :fallback (fn [] #jsx [:p {:class "text-zinc-400 ml-1"} "No Projects in Organization."])}]]
           [c/card {:& {:title "Users"
                        :icon "i-tabler-activity"}}
            [:p {:class "text-zinc-400 ml-1"} "No recent activity."]]
           [c/card {:& {:title "Contracts"
                        :icon "i-tabler-activity"}}
            [:p {:class "text-zinc-400 ml-1"} "No recent activity."]]]]))
