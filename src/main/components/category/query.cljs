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
      creator { id isViewer }
      color
proposals(last: 10) {
        edges {
          node {
            id
            name
            author {
              id
            }
          }
        }
      }
      mainReviewer { id }
      children(first: 100) {
        edges {
          node {
            child {
              name
              color
              id
              creator { id isViewer }
            }
          }
        }
      }
    }
  }
}"))


#_(defn filter-c [thing]
  (cond (vector? thing) (mapv filter-c thing)
        ;(string? thing) (or (second (string/split thing "/")) thing)
        (map? thing) (cond  (zipmap (mapv filter-c (keys thing)) (remove-ns (vals thing))))))
