(ns components.blueprint.dropdown
  (:require ["solid-js" :refer [For Show onMount]]
            ["./button.cljs" :as b]
            ["@solidjs/router" :refer [A]]
            ["flowbite" :refer [initDropdowns]]
            ["./label.cljs" :as l]))

(defn dropdown-select [{:keys [id title items on-change selected extra-class]}]
  #jsx [:div {:id id
              :class (str "flex flex-col " extra-class)}
        [Show {:when (not (nil? title))}
         (l/label title )]
        [:select {:onChange on-change
                  :class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500 p-3 pr-6
                          focus:border-blue-500 block h-10 w-full
                          dark:(bg-black border-zinc-400 ring-zinc-400 placeholder-gray-400 text-white focus:ring-blue-500 focus:border-blue-500) cursor-pointer grow "}
         [For {:each (items)}
          (fn [{:keys [value icon href id] :as item} i]
            #jsx [:option {:selected (if (= id (selected))
                                    true false)}
               value])]]])

(defn dropdown [{:keys [id title items on-change selected extra-class]}]
  (onMount #(initDropdowns))
  #jsx [:div {:id id
              :class "z-10 hidden bg-white divide-y divide-gray-100 rounded-lg shadow w-fit dark:(bg-black divide-gray-600 border-gray-600) border-1"}
        [Show {:when title}
         [:div {:class "px-4 py-3 text-sm text-gray-900 dark:text-white"}
          (title)]]
        [:ul {:class "py-2 text-sm text-gray-700 dark:text-gray-200"}
         #jsx [For {:each (items)}
               (fn [{:keys [value icon img href id] :as item} i]
                 #jsx [:li {:class ""}
                       [A {:href (or href "")
                           :onClick on-change}
                        [:button {:class "w-full flex gap-2 block px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-600 dark:hover:text-white items-center"
                                  :on-click on-change}
                         [Show {:when icon}
                          [:div {:class (str "w-5 h-5 " icon)}]]
                         [Show {:when img}
                          [:img {:class "w-5 h-5 flex items-center justify-center"
                                 :draggable false
                                 :onDragStart nil
                                 :src img}]]
                         value]]])]]])
