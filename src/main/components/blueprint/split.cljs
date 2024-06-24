(ns main.components.blueprint.split)

(defn Split [props]
  #jsx [:div {:class (str "grid grid-cols-1 md:(grid-cols-2 justify-center gap-0) 3xl:grid-cols-3 w-full h-full gap-4 "
                          props.extra-class)}
        props.children])

(defn SplitItem [props]
  #jsx [:div {:class (str "grid col-span-full md:col-span-1 xl:col-span-1 dark:border-gray-600 border-gray-200 px-4 flex flex-col gap-3 " props.extra-class)}
        props.children])
