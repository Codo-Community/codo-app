(ns safe
  (:require ["@safe-global/safe-core-sdk" :as Safe]
            ["@safe-global/sdk-starter-kit" :refer [createSafeClient]]
            ["./client.cljs" :as cli]))

(defonce client (atom nil))

(defonce api-client (atom nil))

;; Constants
(def signer-address "0xYourSignerAddress")
(def signer-private-key "0xYourPrivateKey")
(def rpc-url "https://rpc.ankr.com/eth_sepolia")
(def safe-address "0xYourSafeAddress")

(defn set-api [network address]
  (reset! api-client "https://safe-transaction-{network}.safe.global/api/v1/safes/{safeAddress}/"))

;; Create SafeClient
(defn ^:async create-safe-client [wallet-client safe-address]
  (js-await (createSafeClient
             {:provider wallet-client
              :signer wallet-client
              :safeAddress safe-address})))

(defn ^:async get-owners [safe-client]
  (.getOwners safe-client))

;; Create Safe Transactions
(defn ^:async send-safe-transaction [safe-client transactions]
  (let [transactions [{:to "0xRecipientAddress"
                       :data 0
                       :value "0"}]
        tx-result (js-await (.send safe-client {:transactions transactions}))
        safe-tx-hash (-> tx-result .-transactions .-safeTxHash)]
    (js/console.log (str "Safe Transaction Hash: " safe-tx-hash))
    safe-tx-hash))

;; Confirm the Safe Transaction
(defn ^:async confirm-safe-transaction [safe-client safe-tx-hash]
  (let [pending-transactions (js-await (.getPendingTransactions safe-client))
        pending-results (-> pending-transactions .-results)]
    (doseq [transaction pending-results]
      (when (= (.-safeTxHash transaction) safe-tx-hash)
        (let [confirmation (js-await (.confirm safe-client {:safeTxHash safe-tx-hash}))]
          (js/console.log (str "Transaction confirmed: " confirmation)))))))

;; Full Workflow
#_(defn ^:async execute-safe-transaction []
  (let [safe-client (js-await (create-safe-client))
        safe-tx-hash (js-await (send-safe-transaction safe-client))]
    ;; Assuming a single threshold
    (js-await (confirm-safe-transaction safe-client safe-tx-hash))))

;; Run the workflow
#_(js/await (execute-safe-transaction))


#_(def client ())

#_(defn ^:async init-client [provider signer]
(createSafeClient {:provider provider
                   :signer signer
                   :safeAddress "'0x...'"}))


#_(defn ^:async get-remaining-signatures [safe-sdk safe-transaction]
  (let [threshold (js-await (.getThreshold safe-sdk)) ;; Get the required threshold of signatures
        signatures (.-signatures safe-transaction) ;; Access the current signatures
        remaining-signatures (- threshold (count (js/Object.keys signatures)))] ;; Calculate remaining
    (js/console.log (str "Remaining signatures: " remaining-signatures))
    remaining-signatures))

#_(let [transactions #js [{:to "0xRecipient1"
                                          :value "0"
                                          :data "0x"}
                                         {:to "0xRecipient2"
                                          :value (.toString (ethers/utils.parseEther "0.5"))
                                          :data "0x"}]]
                   ;; Create a batch transaction
                   (.createTransaction safeSdk #js {:safeTransactionData transactions})
                   (.then (fn [safeTransaction]
                            ;; Sign the transaction
                            (.signTransaction safeSdk safeTransaction)
                            (.then (fn [signedSafeTransaction]
                                     ;; Submit the transaction
                                     (let [safe-service (SafeServiceClient. #js {:txServiceUrl "https://safe-transaction.mainnet.gnosis.io"
                                                                                :ethAdapter (.getEthAdapter safeSdk)})]
                                       (.proposeTransaction safe-service
                                                            #js {:safeAddress safe-address
                                                                 :safeTransaction signedSafeTransaction
                                                                 :signerAddress (.getAddress signer)})
                                       (.then (fn []
                                                (js/console.log "Batch transaction submitted successfully!")))))))))

;; Assuming `safe-client` and `transactions` are defined
#_(defn ^:async send-transaction [safe-client transactions]
  (let [tx-result (js-await (.send safe-client #js {:transactions transactions}))
        safe-tx-hash (-> tx-result .-transactions .-safeTxHash)] ;; Safe transaction hash
    safe-tx-hash))

#_(comment
;; Usage Example
(js/await (let [safe-tx-hash (js-await (send-transaction client transactions))]
            (js/console.log (str "Safe Transaction Hash: " safe-tx-hash))))


;; Usage Example
(js/await (let [remaining (js-await (get-remaining-signatures safe-sdk transaction))]
            (js/console.log (str "Signatures left to execute the transaction: " remaining))))
)
