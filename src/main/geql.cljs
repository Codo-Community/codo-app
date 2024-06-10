(ns graphql-eql-transform.core
  (:require ["graphql" :as graphql]
            [squint.string :as str]
            ["lodash" :refer [camelCase startCase]]))

(defn eql-key->field [key]
  (if (vector? key)
    {:kind "Field"
     :name {:kind "Name" :value (first key)}
     :arguments [{:kind "Argument"
                  :name {:kind "Name" :value "id"}
                  :value {:kind "StringValue" :value (second key)}}]}
    {:kind "Field"
     :name {:kind "Name" :value key}}))

(defn inline-fragment [k]
  {:kind "InlineFragment"
   :typeCondition {
                   :kind "NamedType"
                   :name {
                          :kind "Name"
                          :value k
                          }
                   }})

(defn build-field [field]
  #_(println "eq:f " field)
  (let [[k v] (cond
                (vector? field) (if (and (string? (first field)) (vector? (second field)))
                                  field
                                  (mapv build-field field))
                (map? field) [(first (keys field)) (vals field)]
                :else [field nil])]
    (if-not (map? k)
      (let [k (str/split k ",")
            k (if (= (count k) 2) k (first k))]
        (if (vector? k) (assoc (eql-key->field ["node" (second k)])
                               :selectionSet {:kind "SelectionSet"
                                              :selections [(assoc (inline-fragment (startCase (camelCase (first k))))
                                                                  :selectionSet {:kind "SelectionSet"
                                                                                 :selections (mapv build-field v)})]})
            (if (vector? v) (assoc (eql-key->field k) :selectionSet {:kind "SelectionSet"
                                                                     :selections (mapv build-field v)})
                (eql-key->field k))))
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
#_(def eql-query {:viewer [:id {:user [:firstName :lastName]}]})

#_(def eql-query {[:user 0] [:id :firstName :lastName]})

;(println "eq:q" eql-query)
;(println "eq: " (eql->graphql eql-query))
