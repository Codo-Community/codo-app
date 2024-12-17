(ns client
  (:require ["@requestnetwork/request-client.js" :as RequestClient]
            ["@requestnetwork/web3-signature" :refer [Web3SignatureProvider]]
            ["@requestnetwork/payment-detection" :refer [getTheGraphClient]]))

(defonce client (atom nil))

(defn ^:async init-request-client [wallet-client]
  (let [web3-signature-provider (Web3SignatureProvider. wallet-client)
          ;; Define network-specific subgraph URLs
        get-subgraph-client
        (fn [chain]
          (let [payments-subgraph-url
                (cond
                  (= chain "mainnet")
                  (or js/import.meta.env.VITE_NEXT_PUBLIC_PAYMENTS_SUBGRAPH_URL_MAINNET
                      "https://subgraph.satsuma-prod.com/e2e4905ab7c8/request-network--434873/request-payments-mainnet/api")

                  (= chain "matic")
                  (or js/import.meta.env.VITE_NEXT_PUBLIC_PAYMENTS_SUBGRAPH_URL_MATIC
                      "https://subgraph.satsuma-prod.com/e2e4905ab7c8/request-network--434873/request-payments-matic/api")

                  (= chain "sepolia")
                  (or js/import.meta.env.VITE_NEXT_PUBLIC_PAYMENTS_SUBGRAPH_URL_SEPOLIA
                      "https://subgraph.satsuma-prod.com/e2e4905ab7c8/request-network--434873/request-payments-sepolia/api"))]
            (if payments-subgraph-url
              (getTheGraphClient chain payments-subgraph-url)
              (throw (js/Error. (str "Cannot get subgraph client for unknown chain: " chain))))))

          ;; Initialize the Request Network
        request-network
        (RequestClient/RequestNetwork.
         {:nodeConnectionConfig {:baseURL "https://gnosis.gateway.request.network/"}
          :signatureProvider   web3-signature-provider
          :httpConfig          {:getConfirmationMaxRetry 120}
          :paymentOptions      {:getSubgraphClient get-subgraph-client}})]
      ;; Call the setter function with the initialized client
    (reset! client request-network)
    @client))
