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
      children(first: 100) {
        edges {
          node {
            child {
              name
              id
            }
          }
        }
      }
    }
  }
}"))
