(ns main.components.tiptap
  (:require ["solid-js" :refer [createSignal]]
            ["solid-tiptap" :refer [createTiptapEditor createEditorTransaction]]
            ["terracotta" :refer [Toggle]]))

(defn control [props]
  (let [flag (createEditorTransaction
              (fn [] (:editor props))
              (fn [instance]
                (if (:is-active props)
                  ((:is-active props) instance)
                  (.isActive instance (:key props)))))]
    #jsx [Toggle {:& {:defaultPressed false
                      :class (str (:class props) " w-6 h-6 flex items-center justify-center rounded focus:outline-none focus-visible:ring focus-visible:ring-purple-400 focus-visible:ring-opacity-75")
                      :classList {:text-color-600 (flag)
                                  :bg-white (flag)
                                  :bg-opacity-25 (flag)}
                      :title (:title props)
                      :onChange (:on-change props)}}
          (:children props)]))
