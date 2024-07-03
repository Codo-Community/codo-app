(ns Context
  (:require ["solid-js" :refer [createContext createSignal]]
            ["./tiptap/editor.cljs" :refer [create-editor]]))

(def EditorContext (createContext (let [[editor setEditor] (createSignal)
                                        [menu2 setMenu] (createSignal)]
                                    {:editor editor
                                     :setEditor setEditor
                                     :element [editor setEditor]
                                     :menu [menu2 setMenu]
                                     :setMenu setMenu
                                     :comp (create-editor editor menu2 "sadddddddddddddd")})))
