(ns graphql-eql-transform.core
  (:require ["graphql" :as graphql]
            [squint.string :as str]))

(defn eql-key->field [key]
  (if (vector? key)
    {:kind "Field"
     :name {:kind "Name" :value (first key)}
     :arguments (mapv (fn [[k v]]
                        {:kind "Argument"
                         :name {:kind "Name" :value k}
                         :value {:kind "StringValue" :value v}})
                      (partition 2 (rest key)))}
    {:kind "Field"
     :name {:kind "Name" :value key}}))

(defn build-field [field]
  #_(println "f " field)
  (let [[k v] (cond
                (vector? field) (if (and (string? (first field)) (vector? (second field)))
                                  field
                                  (mapv build-field field))
                (map? field) [(first (keys field)) (vals field)]
                :else [field nil])]
    (if-not (map? k)
      (let [base-field (eql-key->field k)]
        (if (vector? v) (assoc base-field :selectionSet {:kind "SelectionSet"
                                                         :selections (mapv build-field v)})
            base-field))
      (vec (flatten [k v])))))

(defn eql->graphql [eql]
  (let [ast {:kind "Document"
             :definitions [{:kind "OperationDefinition"
                            :operation "query"
                            :selectionSet {:kind "SelectionSet"
                                           :selections (mapv build-field eql)}}]}]
    #_(js/console.log ast)
    (graphql/print ast)))

;; Example usage:
(def eql-query {:viewer [:id {:user [:firstName :lastName]}]})

(eql->graphql eql-query)
