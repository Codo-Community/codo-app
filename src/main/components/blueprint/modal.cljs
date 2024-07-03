(ns main.components.blueprint.modal)

#_(defn modal [target title]
  #jsx [:div {:data-modal-target target
              :data-modal-toggle target
              }
        body] )

(defn modal [{:keys [id body]}]
  #jsx [:div {:id id
              :class "hidden overflow-y-auto overflow-x-hidden w-screen p-10 bg-black bg-opacity-60 bg-blend-darken fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full"
              :aria-hidden "true"
              :tabindex "-1"}
        body])
