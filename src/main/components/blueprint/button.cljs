(ns components.blueprint.button
  (:require ["solid-js" :refer [Show]]
            ["../../utils.cljs" :as u]))

(defn button [{:keys [title on-click color icon img img-class extra-class extra-attr class] :as props :or {color "blue" extra-attr {}}}]
  #jsx [:button {:class (or class (str "flex items-center justify-center rounded-md overflow-hidden maxx-w-48 overflow-hidden bg-white rounded-md text-sm p-2 border-1 border-zinc-400 gap-2 dark:(bg-black text-white ring-zinc-400 hover:(ring-white ring-2) active:(ring-blue-400 ring-2)) h-10 w-fit " extra-class))
                 :onClick on-click}
        [Show {:when props.icon}
         [:div {:class (str " " props.icon)}]]
        [Show {:when props.img}
         [:img {:class (or img-class "w-6 h-6 p-1")
                :draggable false
                :onDragStart nil
                :src props.img}]]
        [Show {:when props.title} props.title]
        [Show {:when props.children} props.children]])
