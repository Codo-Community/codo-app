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
            ["./userdropdown.cljs" :as ud]
            ["@solidjs/router" :refer [useNavigate useSearchParams useLocation cache]]
            ["./chain_menu.cljs" :as cm]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def codo-logo (js/URL. "/images/codo_new_black.svg" import.meta.url))

(defc Header [this {:keys [component/id user chain]}]
  (let [navigate (useNavigate)
        location (useLocation)]
    (onMount (fn []
               (.then (cli/await-session (:compose @cli/client))
                      (fn [r]
                        (eu/request-addresses @ec/wallet-client
                                              (fn [ethereum-address]
                                                (user/load-viewer-user #(do (println "add " ctx) (t/add! ctx % {:replace [:component/id :header :user]})))))
                        (eu/add-accounts-changed (fn [ethereum-address]
                                                   (user/load-viewer-user #(do (println "add " %) (t/add! ctx % {:replace [:component/id :header :user]})))))))))
    #jsx [:header {:class "min-w-96"}
          [:nav {:class "text-gray-900
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
           [:div {:class "flex flex-wrap justify-between items-center mx-auto px-6"}
            [:span {:class "flex gap-3"}
             [:a {:draggable "false"
                  :href "#" :class "flex items-center"}
              [:img {:class (str "h-16 w-16 dark:invert") :alt "Codo Logo" :src codo-logo}]]
             [:span {:class "flex items-center dark:text-white font-bold lt-md:hidden max-w-2/3 "}
              [:text (str location.pathname)]]]
            [:div {:class "flex items-center md:order-2 gap-4"}
             #_[:span {:class "lt-md:hidden"}
              [si/SearchInput {:& {:on-submit (fn [signal]
                                                (navigate (str "/search/&search=" (:search (signal)))))}}]]
             [cm/ui-chain-menu #_{:& {:ident chain}}]
             [:button {:class (str "text-gray-600 dark:text-gray-400 w-7 h-7 " (if ((:dark? props)) "i-tabler-sun" "i-tabler-moon"))
                       :onClick (:dark-toggle props)}]
             [user/ui-user {:& {:ident user}}]
             [ud/ui-user-drop-down {:& {:ident user}}]]]]]))

(def ui-header (comp/comp-factory Header AppContext))
