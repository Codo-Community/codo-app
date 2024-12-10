(ns pages.search
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["@solidjs/router" :refer [A]]
            ["./blueprint/icons/web3.cljs" :as wi]
            ["./blueprint/button.cljs" :as b])
  (:require-macros [sqeave :refer [defc]]))

(defc ProjectItem [this {:project/keys [id name description {contract [:contract/id :contract/chain]}]
                         ;:local-storage/keys [bookmarked-projects]
                         }]
  #jsx [A {:href (str "/project/" (id))}
        [:div {:class "border border-white dark:border-black w-full bg-white dark:bg-black p-6 shadow rounded-lg rounded
                hover:border-sky-500 hover:dark:border-sky-500 hover:cursor-pointer dark:border-gray-600"}
         [:div {:class "grid grid-cols-3"}
          [:div {:class "col-span-1 flex items-center border-r border-gray-200 dark:border-gray-600"}
           #_[:img {:src "https://cdn.tuk.dev/assets/components/misc/doge-coin.png"
                    :alt "coin avatar"
                    :class "w-12 h-12 rounded-full"}]
           [:div {:class "flex items-start justify-between w-full"}
            [:div {:class "pl-3 w-full"}
             [:p {:class "focus:outline-none text-xl font-medium leading-5 text-gray-800 dark:text-white"}
              (name)]
             #_[:p {:class "focus:outline-none text-sm leading-normal pt-2 text-gray-500 dark:text-gray-200"}
                "36 contributors"]]
            [:div {:class "w-8 h-8 p-1"}
             [:img {:class "rounded-md"
                    :draggable false
                    :onDragStart nil
                    :src (get wi/icons (:contract/chain (contract)))}]]]]
          [:div {:class "px-2 col-span-2"}
           [:div {:class "flex items-start justify-between w-full"}
            #_(let [icon (if  (some #(= (id) %) (u/get-item :bookmarked-projects)) "i-tabler-bookmark-filled" "i-tabler-bookmark")]
                [:div {:role "img" :aria-label "bookmark"
                       :class (str "hover:i-tabler-bookmark-filled " icon)
                       :onClick #(u/set-item! :bookmarked-projects [(id)])}])]
           [:p {:class "focus:outline-none text-sm leading-5 py-4 text-gray-600 dark:text-gray-200"}
            (or (description) "No comprehensive description yet ...")]
           #_[:div {:tabindex 0 :class "focus:outline-none flex"}
              [For {:each (tags)}
               [:div {:class "py-2 px-4 text-xs leading-3 text-indigo-700 rounded-full bg-indigo-100"} "#dogecoin"]]]]]]])
