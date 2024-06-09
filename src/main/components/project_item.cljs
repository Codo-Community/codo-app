(ns pages.search
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../comp.mjs" :as comp]
            ["@solidjs/router" :refer [A]]
            ["../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defc ProjectItem [this {:project/keys [id name description chain {contract [:contract/chain]}]}]
  #jsx [:div {:class "border border-white dark:border-black w-full bg-white dark:bg-black p-6 shadow rounded-md rounded-md
                hover:border-sky-500 hover:dark:border-sky-500 hover:cursor-pointer dark:border-gray-800"}
        [A {:href (str "/project/" (id))}
         [:div {:class "flex items-center border-b border-gray-200 dark:border-gray-700 pb-6"}
          [:img {:src "https://cdn.tuk.dev/assets/components/misc/doge-coin.png"
                 :alt "coin avatar"
                 :class "w-12 h-12 rounded-full"}]
          [:div {:class "flex items-start justify-between w-full"}
           [:div {:class "pl-3 w-full"}
            [:p {:class "focus:outline-none text-xl font-medium leading-5 text-gray-800 dark:text-white"}
             (name)]
            [:p {:class "focus:outline-none text-sm leading-normal pt-2 text-gray-500 dark:text-gray-200"}
             "36 contributors"]]
           [:div {:class "w-8 h-8 p-1"} (:contract/chain (contract)) #_(get-in chains [:project.chain/polygon :icon])]
           [:div {:role "img" :aria-label "bookmark" :class "i-tabler-bookmark"}]]]]
        [:div {:class "px-2"}
         [:p {:class "focus:outline-none text-sm leading-5 py-4 text-gray-600 dark:text-gray-200"} (description)]
         [:div {:tabindex 0 :class "focus:outline-none flex"}
          [:div {:class "py-2 px-4 text-xs leading-3 text-indigo-700 rounded-full bg-indigo-100"} "#dogecoin"]
          [:div {:class "py-2 px-4 ml-3 text-xs leading-3 text-indigo-700 rounded-full bg-indigo-100"} "#crypto"]]]])

(def ui-project-item (comp/comp-factory ProjectItem AppContext))
