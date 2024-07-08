(ns main.components.category-query)

(defn category-query [id]
  (str "query {
  node(id:  \"" id " \") {
    ... on Category {
      id
      name
      children(first: 10) {
        edges {
          node {
            child {
              name
              id
              children(first: 10) {
                edges {
                  node {
                    child {
                      children(first: 10) {
                        edges {
                          node {
                            child {
                              children(first: 10) {
                                edges {
                                  node {
                                    child {
                                      id
                                      name
                                    }
                                  }
                                }
                              }
                              id
                              name
                            }
                          }
                        }
                      }
                      name
                      id
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}"))


(defn simple [id]
  (str "query {
  node(id: \"" id "\") {
    ... on Category {
      id
      creator { id }
      color
      description
      name
proposals(last: 50) {
        edges {
          node {
            id
            name
            status
            author {
              id
            }
          }
        }
        pageInfo { hasNextPage }
      }
      mainReviewer { id }
      children(last: 50) {
        edges {
          node {
            id
            child {
              name
              color
              description
              id
              creator { id }
            }
          }
        }
        pageInfo { hasNextPage }
      }
    }
  }
}"))


#_(defn filter-c [thing]
  (cond (vector? thing) (mapv filter-c thing)
        ;(string? thing) (or (second (string/split thing "/")) thing)
        (map? thing) (cond  (zipmap (mapv filter-c (keys thing)) (remove-ns (vals thing))))))
