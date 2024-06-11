(ns comp)

(defmacro defc [name bindings body]
  (let [ntmp (str name)
        n (namespace (first (keys (second bindings))))

        params (first (vals (second bindings)))
        val-vec (mapv #(if (map? %) (first (keys %)) %) params)

        keywordify (fn [x] (keyword (str (if n (str n "/")) x)))

        or-map (let [m (-> bindings second :or)]
                 (zipmap (mapv keywordify (keys m)) (vals m)))
        a (println or-map)

        query (mapv #(if (map? %)
                       {(keywordify (first (keys %))) (first (vals %))}
                       (keywordify %)) params)]

    (list 'defclass name
          (list 'extends `Comp)

          (list (with-meta 'field {:static true}) 'query query)

          (list 'constructor ['this# 'ctx] (list 'println "constructor: " ntmp)
                (list 'super 'ctx)
                (list 'set! 'this#.new-data 'this#.constructor.new-data)
                (list 'set! 'this#.render 'this#.constructor.render))
          'Object
          (list (with-meta 'new-data {:static true}) ['_ '& 'data] (list 'let ['v (list 'vals or-map)
                                                                               'k (list 'keys or-map)]
                                                                         (list 'merge or-map (list 'or (list 'first 'data) {}))))

          (list (with-meta 'render {:static true}) ['this# 'props '& 'children]
                (list 'let [(first bindings) 'this#
                            'a (list 'println "render: " ntmp " props: " 'props)
                            'ctx (list `useContext 'this#.ctx)
                            'a (list 'set! 'this#.-ctx 'ctx)
                            'ident (list 'get 'props :ident)
                            'ident (list 'if-not (list 'fn? 'ident) (list 'fn [] 'ident) 'ident)
                            'a (list 'println ntmp ": p " 'props " i: " (list 'ident))
                            {:keys ['store 'setStore]} 'ctx
                            'data (list 'if (list 'comp/ident? (list 'ident))
                                        (list 'do
                                              (list 'set! 'this#.ident 'ident)
                                              (list `createMemo (list 'fn []
                                                                      (list 'println "memo: " ntmp " ident: " (list 'ident) " query: " query)
                                                                      (list 'println "data: " (list `pull 'store (list 'ident) query))
                                                                      (list `pull 'store (list 'ident) query))))
                                        (list 'fn [] 'props))
                            val-vec (mapv #(list 'fn [] (list % (list 'data))) (mapv keywordify val-vec))]
                      body
                      #_(list 'if 'children [:<>
                                             body
                                             'children] body))))))

#_(defmacro defm [name binding & action-forms]
  )
