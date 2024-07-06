(ns main.evm.gc-passport
  (:require ["./client.cljs" :as ec]
            ["../query_client.cljs" :refer [queryClient]]))

(def headers {"Content-Type" "application/json"
              "X-API-KEY" js/import.meta.env.VITE_GITCOIN_API_KEY})
(def SCORERID js/import.meta.env.VITE_GITCOIN_SUBMIT_PASSPORT_SCOREID)

(defn get-signing-message []
  ;; TODO:
  ;; use the tanstack query client from @tanstack/solid-query injected in App.cljs
  (-> (js/fetch js/import.meta.env.VITE_GITCOIN_SIGNING_MESSAGE_URL {:headers headers})
      (.then (fn [response] (.json response)))
      (.then (fn [json] json))
      (.catch (fn [err] (js/console.log "error: " err)))))

(defn ^:async submit-passport-no-verify [address]
  (let [body {:address address
              :scorer_id SCORERID}]
    (.fetchQuery queryClient {:queryKey [:gcp address]
                             :queryFn
                             (fn []
                               (.then (js/fetch js/import.meta.env.VITE_GITCOIN_SIGNING_MESSAGE_URL
                                                {:method "POST"
                                                 :headers headers
                                                 :body (js/JSON.stringify body)})
                                      (fn [response]
                                        (.then (.json response)
                                               (fn [data]
                                                 (println "data:" data)
                                                 data)))))})))


(defn ^:async submit-passport [address]
  (-> (get-signing-message)
      (.then (fn [{:keys [message nonce]}]
               (.then (.signMessage @ec/wallet-client)
                      (fn [signature]
                        (let [body {:address address
                                    :scorer_id SCORERID
                                    :signature signature
                                    :nonce nonce}]
                          (.then (js/fetch js/import.meta.env.VITE_GITCOIN_API_REGISTRY_URL
                                           {:method "POST"
                                            :headers headers
                                            :body (js/JSON.stringify body)})
                                 (fn [response]
                                   (.then (.json response)
                                          (fn [data]
                                            (js/console.log "data:" data))))))))))
      (.catch (fn [err]
                (js/console.log "error:" err)))))

(defn ^:async get-passport-stamps [current-address]
  (let [GET_PASSPORT_STAMPS_URI (str "https://api.scorer.gitcoin.co/registry/stamps/" current-address)]
    (.then (js/fetch GET_PASSPORT_STAMPS_URI {:headers headers})
           (fn [response]
             (.then (.json response)
                    (fn [data]
                      (js/console.log data)))))
    #_(.catch (fn [err]
              (js/console.log "error: " err)))))

(defn ^:async get-passport-score [current-address]
  (let [GET_PASSPORT_SCORE_URI (str "https://api.scorer.gitcoin.co/registry/score/" SCORERID "/" current-address)]
    (.fetchQuery queryClient {:queryKey [:gcp current-address]
                             :queryFn (fn []
                                        (.then (js/fetch GET_PASSPORT_SCORE_URI {:headers headers})
                                               (fn [response]
                                                 (.then (.json response)
                                                        (fn [passportData]
                                                          (if (.-score passportData)
                                                            (let [roundedScore (/ (Math/round (* (.-score passportData) 100)) 100)]
                                                              roundedScore)
                                                            "visit: passport.gitcoin.co"))))
                                               #_(.catch (fn [err]
                                                           (js/console.log "error: " err)))))})))
