(ns main.components.wizards.new-project.contract-step
  (:require ["solid-js" :refer [Show onMount]]
            ["solid-spinner" :as spinner]
            ["@solidjs/router" :refer [HashRouter Route useParams cache]]
            ["../../../evm/client.cljs" :as ec]
            ["../../../evm/lib.cljs" :as el]
            ["../../../evm/util.cljs" :as eu]
            ["../../blueprint/button.cljs" :as b]
            ["../../../transact.cljs" :as t]
            ["../../../composedb/client.cljs" :as cli]
            ["../../../utils.cljs" :as u]
            ["../../../comp.cljs" :as comp]
            ["../../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn contract-mutation []
  (str "mutation CreateContract($i: CreateContractInput!){
          createContract(input: $i){
            document { id name chain address }
 } }"))

(defn update-project-mutation []
  (str "mutation UpdateProject($i: UpdateProjectInput!){
          updateProject(input: $i){
            document { contractID }
 } }"))

(defn  ^:async fetch-abi [pid ctx]
  (let [res (js-await (js/fetch (str js/import.meta.env.VITE_APP_URL "/abi/Project.json")))
        [account] (js-await (.getAddresses @ec/wallet-client))
        data (js-await ((aget res "json")))
        tx (js-await (el/deploy-contract @ec/wallet-client (:abi data) account (:bytecode data)))
        receipt (js-await (.waitForTransactionReceipt @ec/public-client {:hash tx}))]
                                        ; add contract
    (.then (cli/exec-mutation (contract-mutation)
                              {:i {:content {:name "Project" :chain (str (-> @ec/wallet-client :chain :id)) :address receipt.contractAddress}}})
           (fn [response] (let [res (-> response :data :createContract :document)]
                            (println response)
                            (t/add! ctx (u/nsd res :contract) {:replace [:project/id pid :project/contract]})
                            (println {:i {:id pid
                                          :content {:contractID (:id res)}}})
                            ;; replace project contractID
                            (.then (cli/exec-mutation (update-project-mutation)
                                                      {:i {:id pid
                                                           :content {:contractID (:id res)}}})
                                   (fn [response] (let [res (-> response :data :updateProject :document)]
                                                    (println response)))))))))

(defc ContractStep [this {:project/keys [id contract] :as data}]
  (let [params (useParams)]
    #_(onMount (fn [] (when (or (nil? (contract))
                              (= undefined (second (contract)))
                              (u/uuid? (second (contract))))
                      (.then (cli/await-session) #(fetch-abi (:id params) ctx)))))
    #jsx [:div {}
          [Show {:when (and (not (nil? (contract)))
                            (not (= undefined (second (contract))))
                            (not (u/uuid? (second (contract)))))
                 :fallback #jsx [:span {:class "flex gap-4 items-center"}
                                 [b/button {:title "Deploy contract"
                                            :on-click (fn [e] (.then (fetch-abi (:id params) ctx)
                                                                    #(println %)))}]
                                 #_[spinner/TailSpin]]}
           "Project Contract Deployed!"]]))

(def ui-contract-step (comp/comp-factory ContractStep AppContext))
