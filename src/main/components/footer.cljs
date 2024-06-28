(ns main.components.footer)

(defn ui-footer []
  #jsx [:footer {:class "bg-[#f3f4f6] dark:bg-black"}
        [:div {:class "mx-auto w-full max-w-screen-xl"}]
        [:div {:class "p-4 md:flex md:items-center md:justify-between"}
         [:span {:class "text-sm text-gray-400 dark:text-gray-800 sm:text-center"}
          "© 2024 " [:a {:href "https://www.codo.community/"} "Codo™"] ". All Rights Reserved." [:div {:class "i-tabler-github"}]]]])
