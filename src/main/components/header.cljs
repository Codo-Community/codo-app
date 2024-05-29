(ns main.components.header
  (:require ["solid-js" :refer [onMount]]
            ["../comp.mjs" :as comp]
            ["./user.jsx" :as user]
            ["../evm/util.mjs" :as eu]
            ["../evm/client.mjs" :as ec]
            ["./blueprint/searchinput.jsx" :as si]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc Header [this {:keys [comp/id user]}]
  (do (onMount (fn []
                 (eu/request-addresses ec/wallet-client #(user/add-user ctx (first %) {:replace [:comp/id :header :user]}))
                 (eu/add-accounts-changed #(user/add-user ctx (first %) {:replace [:comp/id :header :user]}))))
      #jsx [:header {}
            [:nav {:class "border-gray-200 text-gray-900 px-4
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
             [:div {:class "flex flex-wrap justify-between items-center mx-auto overflow-hidden px-2"}
              [:span {:class "flex gap-3"}
               [:a {:draggable "false"
                    :href "#", :class "flex items-center"} "codo"]]
              [:div {:class "flex items-center md:order-2 md:space-x-5 overflow-hidden py-2"}
               [si/SearchInput {:on-submit (fn [e]
                                             (.preventDefault e)

                                             )}]
               [user/ui-user (user)]]]]]))

(def ui-header (comp/comp-factory Header AppContext))
