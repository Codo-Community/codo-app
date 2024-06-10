(ns comp)

(defmacro defc [name bindings body]
  (let [ntmp (str name)
        n (namespace (first (keys (second bindings))))
        params (first (vals (second bindings)))
        val-vec (mapv #(if (map? %) (first (keys %)) %) params)
        keywordify (fn [x] (keyword (str (if n (str n "/")) x)))
        query (mapv #(if (map? %)
                       {(keywordify (first (keys %))) (first (vals %))}
                       (keywordify %)) params)]

    (list 'defclass name
          (list 'extends `Comp)
          (list (with-meta 'field {:static true}) 'query query)
          (list 'constructor ['_ 'ctx] (list 'println "constructor: " ntmp) (list 'super 'ctx))
          'Object

          (list 'render ['this# 'children]
                (list 'let [(first bindings) 'this#
                            ;'asd (list 'println "chi " 'children)
                            ;'children 'children.children
                            ;'asd (list 'println "chi2 " 'children)
                            ;'asd (list 'println "render: " ntmp)
                            'ctx (list `useContext (list 'this#.ctx))
                            {:keys ['store 'setStore]} 'ctx
                            ;'asd (list 'println "cho " (list 'comp/ident? 'children.children))
                            'data (list 'if (list 'comp/ident? 'children.children)
                                        (list `createMemo (list 'fn []
                                                                (list 'println "memo: " ntmp " ident: " 'children.children " query: " 'this#.query)
                                                                (list `pull 'store 'children.children 'this#.query)))
                                        (list 'fn [] 'children))
                            val-vec (mapv #(list 'fn [] (list % (list 'data))) (mapv keywordify val-vec))]
                      body)))))
