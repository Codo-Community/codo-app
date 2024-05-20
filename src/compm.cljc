(ns compm)

(defmacro defc [name bindings body]
  (let [key-vec (second bindings)
        ns-keys (keys key-vec)
        query (vec ns-keys)]
    #_(list 'squint-compiler-jsx [:div "asd"])

    (list 'defclass name
          (list 'extends 'Comp)
          (list 'field '-query [:user/id :user/ethereum-address])
          (list 'constructor ['_ 'ctx] (list 'super 'ctx))
          'Object

          (list 'render ['this# 'ident]
                (list 'let [{:keys ['store 'setStore]} (list 'useContext (list 'this#.ctx))
                            'data (list 'createMemo (list 'fn [] (list 'pull 'store 'ident 'this#.-query)))
                            ['id 'ethereum-address] [(list 'fn [] (list :user/id (list 'data)))
                                                     (list 'fn [] (list :user/ethereum-address (list 'data)))]]
                      body)))))



(comment (list 'squint-compiler-jsx [:div]))
