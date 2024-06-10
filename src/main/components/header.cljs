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
            ;["/images/codo_new_white.svg" :as codo-logo]
            ["@solidjs/router" :refer [useNavigate useSearchParams useLocation cache]]
            ["./chain_menu.cljs" :as cm]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(def codo-logo (js/URL. "/images/codo_new_white.svg" import.meta.url))

(println codo-logo)

(defc Header [this {:keys [component/id user]}]
  (let [navigate (useNavigate)
        location (useLocation)
        [dark? setDark] (createSignal true)]
    (onMount (fn []
               (.then (cli/await-session (:compose @cli/client))
                      (fn [r]
                        (eu/request-addresses @ec/wallet-client
                                              (fn [ethereum-address]
                                                (user/load-viewer-user #(do (println "add " ctx) (t/add! ctx % {:replace [:component/id :header :user]})))))
                        (eu/add-accounts-changed (fn [ethereum-address]
                                                   (user/load-viewer-user #(do (println "add " %) (t/add! ctx % {:replace [:component/id :header :user]})))))))))
    #jsx [:header {:class ""}
          [:nav {:class "text-gray-900
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
           [:div {:class "flex flex-wrap justify-between items-center mx-auto px-6"}
            [:span {:class "flex gap-3"}
             [:a {:draggable "false"
                  :href "#" :class "flex items-center"}
              [:img {:class "h-16 w-16"
                     :src "/images/codo_new_white.svg"}]]
             [:span {:class "flex items-center dark:text-white font-bold lt-md:hidden truncate"}
              [:text (str location.pathname)]]]
            [:div {:class "flex items-center md:order-2 gap-4"}
             [si/SearchInput {:& {:on-submit (fn [signal]
                                               (navigate (str "/search/&search=" (:search (signal)))))}}]
             [cm/ui-chain-menu {:& {:ident (fn [])}}]
             [:button {:class (str "w-16 h-16 " (if (dark?) "i-tabler-sun" "i-tabler-moon"))
                       :onClick #(setDark (not (dark?)))}]
             [user/ui-user {:& {:ident user}}]
             [ud/ui-user-drop-down {:& {:ident user}}]]]]]))

(def ui-header (comp/comp-factory Header AppContext))
