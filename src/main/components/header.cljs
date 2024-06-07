(ns main.components.header
  (:require ["solid-js" :refer [onMount]]
            ["../comp.mjs" :as comp]
            ["./user.jsx" :as user]
            ["../evm/util.mjs" :as eu]
            ["../evm/client.mjs" :as ec]
            ["./blueprint/searchinput.jsx" :as si]
            ["./chain_menu.jsx" :as cm]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Header [this {:keys [comp/id user]}]
  (do (onMount (fn []
                 (eu/request-addresses @ec/wallet-client #(user/add-user ctx (first %) {:replace [:comp/id :header :user]}))
                 (eu/add-accounts-changed #(user/add-user ctx (first %) {:replace [:comp/id :header :user]}))))
      #jsx [:header {:class ""}
            [:nav {:class "text-gray-900
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
             [:div {:class "flex flex-wrap justify-between items-center mx-auto px-6"}
              [:span {:class "flex gap-3"}
               [:a {:draggable "false"
                    :href "#" :class "flex items-center"}
                [:img {:class "h-16 w-16"
                       :src "./images/codo_new_white.svg"}]]]
              [:div {:class "flex items-center md:order-2 gap-4"}
               [si/SearchInput {:on-submit (fn [e]
                                             (.preventDefault e))}]
               [cm/ui-chain-menu]
               [user/ui-user (user)]]]]]))

(def ui-header (comp/comp-factory Header AppContext))
