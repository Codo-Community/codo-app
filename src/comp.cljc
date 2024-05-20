(ns comp)

(defmacro defc [name bindings body]
  (let [n (namespace (first (keys (second bindings))))
        val-vec (first (vals (second bindings)))
        query (mapv #(keyword (str n "/" %)) val-vec)]

    (list 'defclass name
          (list 'extends `Comp)
          (list 'field '-query query)
          (list 'constructor ['_ 'ctx] (list 'super 'ctx))
          'Object

          (list 'render ['this# 'ident]
                (list 'let [(first bindings) 'this#
                            {:keys ['store 'setStore]} (list `useContext (list 'this#.ctx))
                            'data (list `createMemo (list 'fn [] (list `pull 'store 'ident 'this#.-query)))
                            val-vec (mapv #(list 'fn [] (list % (list 'data))) query)]
                      body)))))
