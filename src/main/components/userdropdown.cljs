(ns components.user
  (:require ["solid-js" :refer [onMount]]
            ["./blueprint/tooltip.cljs" :as tt]
            ["./blueprint/button.cljs" :as b]
            ["@solidjs/router" :refer [A]]
            ["../comp.cljs" :as comp]
            ["../transact.cljs" :as t]
            ["flowbite" :as fb]
            ["../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc UserDropDown [this {:user/keys [id firstName ethereum-address]}]
  (do (onMount #(fb/initFlowbite))
      #jsx [:div {:id "header-user-dropdown"
                  :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-md shadow w-44 dark:(bg-black divide-gray-600 border-gray-600) border-1"}
            [:div {:class "px-4 py-3 text-sm text-gray-900 dark:text-white"}
             [:div {:class "font-medium fond-bold"} (or (firstName) (.substring (or (ethereum-address) "0x0") 0 10))]
                                        ;[:div {:class "truncate"} "name@flowbite.com"]
             ]
            [:ul {:class "py-2 text-sm text-gray-700 dark:text-gray-200"}
             [For {:each [{:title "My Projects" :href (str "/user/" (id) "/projects")}
                          {:title "Profile" :href (str "/user/" (id))}]}
              (fn [v i]
                #jsx [:li
                      [A {:href (:href v)
                          :class "block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white"}
                       (:title v)]])]]
            #_[:div {:class "py-2"}
               [:a {:href "#"
                    :class "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 dark:text-gray-200 dark:hover:text-white"}
                "Sign out"]]]))

(def ui-user-drop-down (comp/comp-factory UserDropDown AppContext))
