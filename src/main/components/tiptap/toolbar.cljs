(ns main.components.tiptap.toolbar
  (:require ["./control.cljs" :as c]))

(defn toolbar-contents [{:keys [editor] :as props}]
  #jsx [:div {:class "p-2 flex space-x-1"}
        [:div {:class "flex space-x-1"}
         [c/control {:key "paragraph"
                     :class "font-bold"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.setParagraph) (.run))
                     :title "Paragraph"}
          [:div {:class "i-streamline-paragraph"}]
          #_[paragraph-icon {:title "Paragraph" :class "w-full h-full m-1"}]]
         [c/control {:key "heading-1"
                     :class "font-bold"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.setHeading {:level 1}) (.run))
                     :is-active #(-> % (.isActive "heading" {:level 1}))
                     :title "Heading 1"}
          "H1"]
         [c/control {:key "heading-2"
                     :class "font-bold"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.setHeading {:level 2}) (.run))
                     :is-active #(-> % (.isActive "heading" {:level 2}))
                     :title "Heading 2"}
          "H2"]]
        #_[separator]
        [:div {:class "flex space-x-1"}
         [c/control {:key "bold"
                     :class "font-bold"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleBold) (.run))
                     :title "Bold"}
          "B"]
         [c/control {:key "italic"
                     :class "italic"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleItalic) (.run))
                     :title "Italic"}
          "I"]
         [c/control {:key "strike"
                     :class "line-through"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleStrike) (.run))
                     :title "Strike Through"}
          "S"]
         [c/control {:key "link"
                     :class "link"
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.extendMarkRange "link") (.setLink {:href ""}) (.run))
                     :title "Strike Through"}
          [:div {:class "i-tabler-link"}]]
         #_[c/control {:key "code"
                       :class ""
                       :editor editor
                       :on-change #(-> editor (.chain) (.focus) (.toggleCode) (.run))
                       :title "Code"}
            [:div {}] #_[code-icon {:title "Code" :class "w-full h-full m-1"}]]]
        #_[separator]
        [:div {:class "flex space-x-1"}
         [c/control {:key "bulletList"
                     :class ""
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleBulletList) (.run))
                     :title "Bullet List"}
          [:div {:class "i-tabler-list"}]
          #_[bullet-list-icon {:title "Unordered List" :class "w-full h-full m-1"}]]
         [c/control {:key "orderedList"
                     :class ""
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleOrderedList) (.run))
                     :title "Ordered List"}
          [:div {:class "i-tabler-list-numbers"}]
          #_[ordered-list-icon {:title "Ordered List" :class "w-full h-full m-1"}]]
         [c/control {:key "blockquote"
                     :class ""
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleBlockquote) (.run))
                     :title "Blockquote"}
          [:div {:class "i-tabler-quote"}]
          #_[blockquote-icon {:title "Blockquote" :class "w-full h-full m-1"}]]
         [c/control {:key "codeBlock"
                     :class ""
                     :editor editor
                     :on-change #(-> editor (.chain) (.focus) (.toggleCodeBlock) (.run))
                     :title "Code Block"}
          [:div {:class "i-tabler-code"}]
          #_[code-block-icon {:title "Code Block" :class "w-full h-full m-1"}]]]])
