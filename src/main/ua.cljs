(ns ua
  (:require ["blo" :refer [blo]]
            ["./comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

(defc User [this {:user/keys [id ethereum-address]}]
  #jsx [:div {:class "flex flex-inline  items-caenter justify-items-center text-white"}
        [:img {:class "rounded-lg w-11 h-11 rounded-p md-0.5 text-gray-500 hover:bg-gray-100
                       focus:outline-none focus:ring-4 focus:ring-gray-200 dark:text-gray-400
                       dark:hover:bg-gray-700 dark:focus:ring-gray-700 select-none"
               :draggable false
               :src (blo (ethereum-address))}]])
