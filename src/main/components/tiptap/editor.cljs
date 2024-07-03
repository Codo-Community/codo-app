(ns main.components.tiptap.editor
  (:require ["./toolbar.cljs" :as t]
            ["@tiptap/extension-bubble-menu" :refer [BubbleMenu]]
            ["@tiptap/starter-kit" :refer [StarterKit]]
            ["@tiptap/extension-link" :refer [Link]]
            ["@tiptap/extension-image" :refer [Image]]
            ["solid-js" :refer [createSignal Show createEffect]]
            ["solid-tiptap" :refer [createTiptapEditor useEditorHTML]]
            ["terracotta" :refer [Toolbar]]
            ))

(defn create-editor [editor menu]
  (createTiptapEditor
   (fn []
     {:element (editor)
      :extensions [StarterKit
                   Link
                   Image
                   (.configure BubbleMenu {:element (menu)})]
      :editorProps {:attributes {:class "p-4 max-w-screen mx-4 max-h- focus:outline-none prose max-w-full whitespace-pre-wrap break-words"}}
      :content "<p>Hello World! 🌍️</p>"})))

(defn editor [{:keys [id on-html-change]}]
  (let [[element setElement] (createSignal)
        [menu setMenu] (createSignal)
        editor (create-editor element menu)
        html (useEditorHTML editor)]
        (createEffect #(on-html-change (html)))
        #jsx [:div {:class "flex items-center justify-center"}
              [Toolbar {:& {:ref setMenu
                            :class "dynamic-shadow bg-gradient-to-bl from-indigo-500 to-blue-600 text-white rounded-lg"
                            :horizontal true}}
               [Show {:when (editor) :keyed true}
                (fn [instance]
                  #jsx [t/toolbar-contents {:& {:editor instance}}])]]
              [:div {:class "overflow-y-scroll rounded-lg border-1 dark:border-zinc-500 max-w-full"
                     :ref setElement}]]))
