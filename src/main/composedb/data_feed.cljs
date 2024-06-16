(ns main.composedb.data-feed
  (:require ["cross-eventsource" :as es]
            ["@ceramicnetwork/codecs" :refer [JsonAsString AggregationDocument]]
            ["@ceramicnetwork/streamid" :refer [StreamID]]
            ["codeco" :refer [decode]]))

(def source (es/EventSource. "http://localhost:7007/api/v0/feed/aggregation/documents"))

(def Codec (-> JsonAsString (.pipe AggregationDocument)))

(defn init-listeners [ctx & f]
  (.addEventListener source "message"
                     (fn [event]
                       (let [parsed-data (decode Codec (.-data event))
                             data (:content (decode Codec (.-data event)))
                             instance-id (.toString (-> parsed-data :commitId :baseID))
                             model-id (.toString (-> parsed-data :metadata :model :baseID))]
                         (println "model-id: " model-id)
                         (println "instance-id: " instance-id)
                         (when (first f)
                           ((first f) ctx data instance-id model-id)))))
  (.addEventListener source "error"
                     (fn [error]
                       (println "error " error)))
  (println "listening..."))
