(ns main.components.blueprint.card)

(defn card [{:keys [title icon children extra-class] :as props}]
  #jsx [:div {:class (str "border-zinc-800 p-2 rounded-lg select-none md:h-full h-fit " extra-class)}
        [:span {:class "flex gap-2 mb-2"}
         [:div {:class (str "w-6 h-6 " icon)}]
         [:h1 {:class "font-bold text-xl"} title]]
        children])
