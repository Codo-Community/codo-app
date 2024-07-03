(ns main.components.tiptap.editor
  (:require ["./toolbar.cljs" :as t]
            ["@tiptap/extension-bubble-menu" :refer [BubbleMenu]]
            ["@tiptap/starter-kit" :refer [StarterKit]]
            ["@tiptap/extension-link" :refer [Link]]
            ["@tiptap/extension-image" :refer [Image]]
            ["solid-js" :refer [createSignal Show createEffect createMemo]]
            ["solid-tiptap" :refer [createTiptapEditor useEditorHTML useEditorIsFocused]]
            ["terracotta" :refer [Toolbar]]))

(defn create-editor [editor menu content]
  (createTiptapEditor
   (fn []
     {:element (editor)
      :extensions [StarterKit
                   Link
                   Image
                   (.configure BubbleMenu {:element (menu)})]
      :editorProps {:attributes {:class "p-4 max-w-screen mx-4 max-h- focus:outline-none prose max-w-full whitespace-pre-wrap break-words"}}
      :content content})))

(defn editor [{:keys [on-html-change element menu comp]}]
  (let [[element setElement] (or element (createSignal))
        [menu setMenu] (or menu (createSignal))
        editor (or comp (create-editor element menu ""))
        html (useEditorHTML editor)
        active? (useEditorIsFocused editor)]
    (createEffect #(when (active?) (on-html-change (html))))
    #jsx [:div {:class "flex items-center justify-center w-full"}
          [Toolbar {:& {:ref setMenu
                        :class "dynamic-shadow bg-gradient-to-bl from-indigo-500 to-blue-600 text-white rounded-lg"
                        :horizontal true}}
           [Show {:when (editor) :keyed true}
            (fn [instance]
              #jsx [t/toolbar-contents {:& {:editor instance}}])]]
          [:div {:class "overflow-y-scroll rounded-lg border-1 dark:border-zinc-500 max-w-full"
                 :ref setElement}]]))
