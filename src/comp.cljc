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
          (list 'field '-query query)
          (list 'constructor ['_ 'ctx] (list 'println "constructor: " ntmp) (list 'super 'ctx))
          'Object

          (list 'render ['this# 'ident]
                (list 'let [(first bindings) 'this#
                            'asd (list 'println "render: " ntmp)
                            'ctx (list `useContext (list 'this#.ctx))
                            {:keys ['store 'setStore]} 'ctx
                            'data (list `createMemo (list 'fn []
                                                          (list 'println "memo: " ntmp " ident: " 'ident.children)
                                                          (list `pull 'store 'ident.children 'this#.-query)))
                            val-vec (mapv #(list 'fn [] (list % (list 'data))) (mapv keyword val-vec))]
                      body)))))
