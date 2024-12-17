(ns request-finance-escrow
  (:require ["@requestnetwork/request-client.js" :refer [RequestNetwork Types Utils]]
            ["@requestnetwork/payment-processor" :refer [payRequest]]
            ["@requestnetwork/web3-signature" :refer [Web3SignatureProvider]]
            #_["@requestnetwork/currency" :refer [CurrencyManager]]
            ["../evm/client.cljs" :as cli]
            ["@w3t-ab/sqeave" :as sqeave]
            ["@ethersproject/providers" :as providers :refer [Web3Provider]]
            [viem :as viem]))

(declare providers)

(defn wallet-client-to-signer-viem [wallet-client]
  (let [{:keys [account chain transport]} wallet-client
        signer {:address (.-address account)
                :chain-id (.-id chain)
                :transport transport}]
    signer))

(defn wallet-client-to-signer [wallet-client]
  (let [{:keys [account chain transport]} wallet-client
        network {:chainId (.-id chain)
                 :name (.-name chain)
                 :ensAddress (-> chain .-contracts .-ensRegistry .-address)}
        provider (Web3Provider. transport network)
        signer (.getSigner provider (.-address account))]
    signer))

(defonce request-client (atom nil))

;; Initialize the Request client
(defn init-request-client []
  (let [client (RequestNetwork. {;:useMockStorage true
                                 :nodeConnectionConfig {:baseURL "https://sepolia.gateway.request.network/"}
                                 :signatureProvider (Web3SignatureProvider. @cli/wallet-client)})]
    (reset! request-client client)
    (println "cli: " client)))

#_(defn viem-connector [wallet-client]
  {:getBalance (fn [address currency]
                 (let [currency-address (:value currency)]
                   (if (= (:type currency) "ETH")
                     (.getBalance wallet-client {:address address})
                     (throw (js/Error. "ERC20 balances are not yet implemented")))))
   :estimateGas (fn [transaction]
                  (.estimateGas wallet-client transaction))
   :sendTransaction (fn [transaction]
                      (.sendTransaction wallet-client transaction))
   :getSignerAddress (fn []
                       (let [account (.-account wallet-client)]
                         (if account
                           account
                           (throw (js/Error. "Wallet client has no account")))))})

#_(defn init-payment-processor [wallet-client]
  (let [connector (viem-connector wallet-client)]
    (PaymentProcessor. {:connector connector})))

#_(defn ^:async pay-request1 [request wallet-client]
  (let [payment-processor (init-payment-processor wallet-client)
        payment-details (js-await (.getPaymentDetails payment-processor request))
        tx (js-await (.pay payment-processor payment-details))]
    (js/console.log "Transaction sent! Hash:" (:hash tx))
    tx))


#_{
   :symbol "ETH"
   :address "eth"
   :network "sepolia"
   :decimals 18
   :type Types.RequestLogic.CURRENCY.ETH
   }

;; Function to fetch the ETH currency object for Sepolia
#_(defn get-eth-sepolia-currency []
  (let [currency-manager (CurrencyManager.)
        currency (.getCurrency currency-manager "ETH" "sepolia" "ETH")] ;; ETH on Sepolia
    (if currency
      (do
        (js/console.log "Currency Object:" currency)
        currency)
      (throw (js/Error. "Currency not found!")))))

(defn native-payment [payment-recipient fee-recipient payee-identity payer-identity expected-amount network fee-amount content-data]
  {:paymentNetwork {:id Types.Extension.PAYMENT_NETWORK_ID.ETH_FEE_PROXY_CONTRACT
                    :parameters {:paymentNetworkName network
                                 :paymentAddress payment-recipient
                                 :feeAddress fee-recipient
                                 :feeAmount (str fee-amount)}}

   :requestInfo {:currency {:type Types.RequestLogic.CURRENCY.ETH
                            :value "ETH"
                            :network network}
                 :expectedAmount (str expected-amount)
                 :payee {"type" Types.Identity.TYPE.ETHEREUM_ADDRESS
                         "value" payee-identity}
                 :payer {"type" Types.Identity.TYPE.ETHEREUM_ADDRESS
                         "value" payer-identity}}
   :contentData (or content-data {})
   :signer {"type" Types.Identity.TYPE.ETHEREUM_ADDRESS
            "value" payer-identity}})

(defn erc20-payment [payee-identity payer-identity payment-recipient fee-recipient expected-amount currency network reason due-date signer fee-amount]
  {"requestInfo" {"currency" {"type" Types.RequestLogic.CURRENCY.ERC20
                              "value" currency
                              "network" network}

                  "expectedAmount" (str expected-amount)
                  "payee" {"type" Types.Identity.TYPE.ETHEREUM_ADDRESS
                           "value" payee-identity}
                  "payer" {"type" Types.Identity.TYPE.ETHEREUM_ADDRESS
                           "value" payer-identity}
                  "timestamp" (Utils.getCurrentTimestampInSecond)}

   "paymentNetwork" {"id" Types.Extension.PAYMENT_NETWORK_ID.ERC20_FEE_PROXY_CONTRACT
                     "parameters" {"paymentNetworkName" network
                                   "paymentAddress" payment-recipient
                                   "feeAddress" fee-recipient
                                   "feeAmount" fee-amount}}

   "contentData" {"reason" reason
                  "dueDate" due-date}

   "signer" {"type" Types.Identity.TYPE.ETHEREUM_ADDRESS
             "value" signer}})

;; Function to create a request with specific parameters
(defn ^:async create-request [client request-data]
  (let [response (js-await (.createRequest client request-data))]
    (println "Request created:" response)
    response))

;; Function to pay an escrow request
(defn ^:async pay-request [client request-id wallet-client]
  (let [contract (js-await (.getContract client request-id wallet-client))
        tx (js-await (.approvePayment contract))] ;; Approve the payment on the smart contract
    (js/console.log "Payment approved. Transaction Hash:" (:hash tx))
    tx))

(defn ^:async from-req-id [client id]
  (.fromRequestId client id))


(defn ^:async pay [data wallet-client]
  (let [signer (wallet-client-to-signer wallet-client)]
    (println "req:" data)
    (println signer)
    (payRequest data signer)))

#_(defn ^:async main []
    (let [client (init-request-client) ;; Initialize the client
          payer-safe "0xPayerSafeAddress"
          payee-safe "0xPayeeSafeAddress"
          amount "5.0"     ;; Amount to be escrowed
          currency "ETH"   ;; Cryptocurrency (e.g., ETH, DAI)
          fee-amount "0.5" ;; Fee amount
          fee-recipient-address "0xSeparateSafeAddress"
          private-key "YourPrivateKeyHere"] ;; Private key of the Safe wallet

      (let [request (js-await (create-escrow-request @request-client payer-safe payee-safe amount currency fee-amount fee-recipient-address))]
        (js-await (pay-escrow-request client (.-requestId request) payer-safe private-key)))))
