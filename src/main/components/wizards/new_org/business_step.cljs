(ns main.components.wizards.new-organization.contract-step
  (:require ["solid-js" :refer [Show onMount]]
            ["solid-spinner" :as spinner]
            ["@w3t-ab/sqeave" :as sqeave]
            ["@solidjs/router" :refer [HashRouter Route useParams cache]]
            ["../../../evm/client.cljs" :as ec]
            ["../../../evm/lib.cljs" :as el]
            ["../../../evm/util.cljs" :as eu]
            ["../../blueprint/button.cljs" :as b]
            ["../../../composedb/client.cljs" :as cli])
  (:require-macros [sqeave :refer [defc]]))

(defn contract-mutation []
  (str "mutation CreateContract($i: CreateContractInput!){
          createContract(input: $i){
            document { id name chain address }
 } }"))

(defn update-organization-mutation []
  (str "mutation UpdateOrganization($i: UpdateOrganizationInput!){
          updateOrganization(input: $i){
            document { contractID }
 } }"))

(defn  ^:async fetch-abi [pid ctx]
  (let [res (js-await (js/fetch (str js/import.meta.env.VITE_APP_URL "/abi/Organization.json")))
        [account] (js-await (.getAddresses @ec/wallet-client))
        data (js-await ((aget res "json")))
        tx (js-await (el/deploy-contract @ec/wallet-client (:abi data) account (:bytecode data)))
        receipt (js-await (.waitForTransactionReceipt @ec/public-client {:hash tx}))]
                                        ; add contract
    (.then (cli/exec-mutation (contract-mutation)
                              {:i {:content {:name "Organization" :chain (str (-> @ec/wallet-client :chain :id)) :address receipt.contractAddress}}})
           (fn [response] (let [res (-> response :data :createContract :document)]
                            (println response)
                            (sqeave/add! ctx (sqeave/nsd res :contract) {:replace [:organization/id pid :organization/contract]})
                            (println {:i {:id pid
                                          :content {:contractID (:id res)}}})
                            ;; replace organization contractID
                            (.then (cli/exec-mutation (update-organization-mutation)
                                                      {:i {:id pid
                                                           :content {:contractID (:id res)}}})
                                   (fn [response] (let [res (-> response :data :updateOrganization :document)]
                                                    (println response)))))))))

(defc BusinessStep [this {:organization/keys [id contract] :as data}]
  (let [params (useParams)]
    #_(onMount (fn [] (when (or (nil? (contract))
                              (= undefined (second (contract)))
                              (sqeave/uuid? (second (contract))))
                      (.then (cli/await-session) #(fetch-abi (:id params) ctx)))))
    #jsx [:div {}
          [Show {:when (and (not (nil? (contract)))
                            (not (= undefined (second (contract))))
                            (not (sqeave/uuid? (second (contract)))))
                 :fallback #jsx [:span {:class "flex gap-4 items-center"}
                                 [b/button {:title "Deploy contract"
                                            :on-click (fn [e] (.then (fetch-abi (:id params) ctx)
                                                                    #(println %)))}]
                                 #_[spinner/TailSpin]]}
           "Organization Contract Deployed!"]]))
