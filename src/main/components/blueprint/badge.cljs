(ns main.components.blueprint.badge)

(defn badge [{:keys [title]}]
  #jsx [:span {:class "bg-purple-100 text-purple-800 text-xs font-bold me-2 px-2.5 py-0.5 rounded dark:bg-black dark:text-purple-400 border-purple-600 border-2"} title])
