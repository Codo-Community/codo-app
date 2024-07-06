(ns main.components.blueprint.indicator)

(defn indicator [{:keys [extra-class] :as props}]
  #jsx [:span {:class (str "absolute inline-flex items-center justify-center w-4 h-4 text-xs font-bold text-white dark:text-white
                            rounded-full -top-1 -left-1.5 border-zinc-400 border-1 " extra-class)}
        props.children])
