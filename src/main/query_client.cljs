(ns main.query-client
  (:require ["@tanstack/solid-query" :refer [QueryClient]]))

(defonce queryClient (QueryClient.))
