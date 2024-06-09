(ns main.components.header
  (:require ["solid-js" :refer [onMount createSignal]]
            ["../comp.mjs" :as comp]
            ["./user.jsx" :as user]
            ["../evm/util.mjs" :as eu]
            ["../evm/client.mjs" :as ec]
            ["../composedb/client.mjs" :as cli]
            ["../transact.mjs" :as t]
            ["./blueprint/searchinput.jsx" :as si]
            ["./blueprint/dropdown.jsx" :as dr]
            ["./userdropdown.jsx" :as ud]
            ["@solidjs/router" :refer [useNavigate useSearchParams useLocation cache]]
            ["./chain_menu.jsx" :as cm]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Header [this {:keys [comp/id user]}]
  (let [navigate (useNavigate)
        location (useLocation)
        params (useSearchParams)
        [dark? setDark] (createSignal true)]
    (onMount (fn []
               (.then (cli/await-session (:compose @cli/client))
                      (fn [r]
                        (eu/request-addresses @ec/wallet-client
                                              (fn [ethereum-address]
                                                (user/load-viewer-user #(t/add! ctx % {:replace [:comp/id :header :user]}))))
                        (eu/add-accounts-changed (fn [ethereum-address]
                                                   (user/load-viewer-user #(t/add! ctx % {:replace [:comp/id :header :user]}))))))))
    #jsx [:header {:class ""}
          [:nav {:class "text-gray-900
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
           [:div {:class "flex flex-wrap justify-between items-center mx-auto px-6"}
            [:span {:class "flex gap-3"}
             [:a {:draggable "false"
                  :href "#" :class "flex items-center"}
              [:img {:class "h-16 w-16"
                     :src "./images/codo_new_white.svg"}]]
             [:span {:class "flex items-center dark:text-white font-bold"}
              [:text (str location.pathname)]]]
            [:div {:class "flex items-center md:order-2 gap-4"}
             [si/SearchInput {:on-submit (fn [signal]
                                           (navigate (str "/search/&search=" (:search (signal)))))}]
             [cm/ui-chain-menu]
             [:button {:class (str "w-16 h-16 " (if (dark?) "i-tabler-sun" "i-tabler-moon"))
                       :onClick #(setDark (not (dark?)))}]
             [user/ui-user (user)]
             [ud/ui-user-drop-down (user)]]]]]))

(def ui-header (comp/comp-factory Header AppContext))
