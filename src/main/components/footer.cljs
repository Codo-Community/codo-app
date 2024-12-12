(ns main.components.footer)

(defn Footer []
  #jsx [:footer {:class "bg-[#f3f4f6] dark:bg-black select-none"}
        [:div {:class "mx-auto w-full max-w-screen-xl"}]
        [:div {:class "p-4 md:flex md:items-center md:justify-between"}
         [:span {:class "text-sm text-gray-400 dark:text-gray-800 sm:text-center flex flex-row gap-2 items-center truncate"}
          "© 2024 " [:a {:href "https://www.codo.community/"} "Codo™"]
          [:div {:class "i-tabler-github"}]
          [:span {:class "flex justify-end items-center w-full gap-1.5"}
           "Powered by: "
           [:a {:href "https://www.ethereum.org/"}
            [:img {:class "dark:filter-invert h-6 opacity-50"
                   :src (str js/import.meta.env.VITE_PINATA_URL "/images/ethereum.png")}]]
           [:a {:href "https://www.clojure.org/"}
            [:img {:class "h-6 opacity-50"
                   :src (str js/import.meta.env.VITE_PINATA_URL "/images/clojure.svg")}]]
           [:a {:href "https://www.ceramic.network/"}
            [:img {:class "h-6 opacity-50"
                   :src (str js/import.meta.env.VITE_PINATA_URL "/images/ceramic-icon.svg")}]]
           [:a {:href "https://www.solidjs.com/"}
            [:img {:class "h-6 opacity-50"
                   :src (str js/import.meta.env.VITE_PINATA_URL "/images/solid.svg")}]]]]]])
