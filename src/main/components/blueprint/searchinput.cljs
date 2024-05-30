(ns main.components.blueprint.searchinput
  (:require ["solid-js" :refer [createMemo useContext createSignal]]
            ["solid-icons/hi" :refer [HiOutlineMagnifyingGlass]]))

(defn SearchInput [{:keys [on-submit]}]
  (let [[local setLocal] (createSignal {:search ""})]
    #jsx [:form {:class "mr-3"
                 :onSubmit on-submit}
          [:label {:htmlFor "default-search"
                   :class "mb-2 text-sm font-medium text-zinc-900 sr-only dark:text-white"}
           "Search"]
          [:div {:class "relative"}
           [:div {:class "absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none"}
            [HiOutlineMagnifyingGlass]]
           [:input {:type "search"
                    :id "default-search"
                    :class "dark:bg-black bg-white block w-full h-11 p-4 pl-10 text-sm text-gray-900 border
                                border-gray-300 rounded-lg
                                focus:ring-blue-500 focus:border-blue-500
                                dark:border-gray-700 dark:placeholder-gray-400 dark:text-white
                                dark:focus:ring-blue-500 dark:focus:border-blue-500"
                    :placeholder "Search ..."
                    :onChange #(setLocal {:search (-> % :target :value)})
                    :value (:search (local))}]]]))
