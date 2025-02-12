(ns main.components.blueprint.tooltip)

(defn tooltip [{:keys [id content]}]
  #jsx [:div {:id id
              :role "tooltip"
              :class "absolute z-10 invisible inline-block px-3 py-2 text-sm font-medium text-white transition-opacity duration-300
                      dark:bg-black rounded-md shadow-sm opacity-0 tooltip dark:bg-black dark:border-zinc-400 border"}
        content
        [:div {:class "tooltip-arrow"
               :data-popper-arrow ""}]])
