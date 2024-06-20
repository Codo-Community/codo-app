(ns main.components.header
  (:require ["solid-js" :refer [onMount createSignal]]
            ["../comp.cljs" :as comp]
            ["./user.cljs" :as user]
            ["../evm/util.cljs" :as eu]
            ["../evm/client.cljs" :as ec]
            ["../composedb/client.cljs" :as cli]
            ["../transact.cljs" :as t]
            ["./blueprint/searchinput.cljs" :as si]
            ["./blueprint/dropdown.cljs" :as dr]
            ["@solidjs/router" :refer [useNavigate useSearchParams useLocation cache]]
            ["../composedb/client.cljs" :as cdb]
            ["./chain_menu.cljs" :as cm]
            ["./blueprint/button.cljs" :as b]
            ["../Context.cljs" :refer [AppContext]]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

(def codo-logo (js/URL. "/images/codo_new_black.svg" import.meta.url))

(defc Header [this {:keys [component/id {user [:user/id :user/session]} chain active-project ]}]
  (let [navigate (useNavigate)
        location (useLocation)]
    (onMount (fn []
               ((user/init-auth ctx))
               (eu/add-accounts-changed (user/init-auth ctx))))
    #jsx [:header {:class "min-w-96"}
          [:nav {:class "text-gray-900
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
           [:div {:class "flex flex-wrap justify-between items-center mx-auto px-4 py-1"}
            [:span {:class "flex gap-3"}
             [:a {:draggable "false"
                  :href "#" :class "flex items-center"}
              [:img {:class (str "h-16 w-16 dark:invert") :alt "Codo Logo" :src codo-logo}]]
             [:span {:class "flex items-center dark:text-white font-bold lt-md:hidden max-w-2/3 "}
              (let [p (second (string/split location.pathname "/"))]
                p)]]
            [:div {:class "flex items-center md:order-2 gap-4"}
             #_[:span {:class "lt-md:hidden"}
                [si/SearchInput {:& {:on-submit (fn [signal]
                                                  (navigate (str "/search/&search=" (:search (signal)))))}}]]
             [:button {:class (str "text-gray-600 dark:(text-zinc-400 hover:text-white) w-7 h-7 " (if ((:dark? props)) "i-tabler-sun" "i-tabler-moon"))
                       :onClick (:dark-toggle props)}]
             [cm/ui-chain-menu #_{:& {:ident chain}}]
             [Show {:when (:user/session (user)) :fallback #jsx [b/button {:title "Log in"
                                                                           :on-click (fn [e]
                                                                                       (eu/request-addresses @ec/wallet-client
                                                                                                             (user/init-auth ctx)))}]}
              [user/ui-user {:& {:ident (fn [] [:user/id (:user/id (user))])}}]]
             [dr/dropdown {:id "header-user-dropdown"
                           :items (fn [] [{:value "My Projects" :href (str "/user/" (:user/id (user)) "/projects") :icon "i-tabler-stack"}
                                          {:value "Profile" :href (str "/user/" (:user/id (user))) :icon "i-tabler-user-circle"}])}]]]]]))

(def ui-header (comp/comp-factory Header AppContext))
