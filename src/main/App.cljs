(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["solid-js/store" :refer [createStore]]
            ["./pages/tbpage.jsx" :as tbp]
            ["./pages/home.jsx" :as home]
            ["./pages/profile.jsx" :as profile]
            ["./pages/search.jsx" :as sp]

            ["./components/blueprint/input.jsx" :as in]
            ["./components/project_report.jsx" :as pr]
            ["./components/userprofile.jsx" :as up]
                                        ;["./components/wizards/new_project/main.jsx" :refer [WizardNewProject]]
            ["./imports.mjs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/info_step.jsx" :as istep]
            ["./components/wizards/new_project/contract_step.jsx" :as cstep]
            ["./normad.mjs" :as norm]
            ["./composedb/client.mjs" :as cdb]
            ["./composedb/auth.mjs" :as cda]
            ["./composedb/composite.mjs" :as composite]
            ["@solidjs/router" :refer [HashRouter Route useParams cache]]
            ["flowbite-datepicker" :as dp]
            ["./utils.mjs" :as utils]
            ["./components/header.jsx" :as h]
            ["./comp.mjs" :as comp :refer [Comp]]
            ["./Context.mjs" :refer [AppContext]]
            ["./transact.mjs" :as t]
            ["flowbite" :as fb]
            ["./geql.mjs" :as geql]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

#_(defn import-proj [comp-path f] (.then (import comp-path) (fn [r] (println "got r " (:WizardNewProject r)) (get r f))))

#_(defn ^:async import-p [path] (println "importing " path) (import path))

#_(.then (import-p "./components/wizards/new_project/main.jsx") (fn [r]
                                                                (println "got r " (:WizardNewProject r))
                                                                (reset! WizardNewProject (:WizardNewProject r)))) #_(import-proj "./components/wizards/new_project/main.jsx" :WizardNewProject)

#_(println "w " (get WizardNewProject "sad"))
#_(println "w " WizardNewProject)

#_(def WizardNewProject (lazy (fn [] (import-p "./components/wizards/new_project/main"))))

#_(js/console.log WizardNewProject)

#_(defn ^:async WizardNewProject [a] (let [res (.then (import-p "./components/wizards/new_project/main") (fn [r]
                                                                                                           (println "got r " (:WizardNewProject r))
                                                                                                           (reset! a (:WizardNewProject r))))]
                                       res))

#_(println (geql/eql->graphql {[:viewer/id "123"] [:id {:user [:firstName :lastName]}]}))
#_(println (geql/eql->graphql {:viever [:id {:user [:firstName :lastName]}]}))

(defn Main [props]
  (let [header [:comp/id :header]]
    #jsx [:div {:class "flex h-screen w-screen flex-col overflow-hidden text-gray-900 dark font-mono dark:text-white bg-[#f3f4f6] dark:bg-[#101014]"}
          [h/ui-header header]
          [:div {:class "flex h-screen w-sceen overflow-auto dark:text-white bg-[#f3f4f6] dark:bg-[#101014] justify-center"}
             props.children]]))

(def get-user (cache (fn [ident] (let [ctx (useContext AppContext)] (println "load2")
                                      (up/load-user-profile ctx ident)))))

(defn load-user [{:keys [params location]}]
  (get-user [:user (:id params)]))

(defn Root []
  (let [[store setStore] (createStore {:viewer []
                                       :counters [{:counter/id 0
                                                   :counter/value 1}]
                                       :component/id {:project-wizard {:project []}
                                                      :project-list {:projects []}}
                                       :pages/id {:profile {:user [:user/id 0]}
                                                  :transaction-builder {:contracts []
                                                                        :contract nil
                                                                        :transactions []}}
                                       :user/id {0 {:user/id 0
                                                    :user/ethereum-address "0x0"}}
                                       :comp/id {:header {:user [:user/id 0]}}})
        {:keys [store setStore] :as ctx} (norm/add {:store store :setStore setStore})]
    (onMount #(do
                (fb/initFlowbite)
                (.then (composite/fetch-abi)
                       (fn [] (.then (cda/init-auth)
                                     (fn [] (.then (cdb/init-clients)
                                                   (fn []))))))))
    #jsx [AppContext.Provider {:value ctx}
          [HashRouter {:root Main}
           [Route {:path "/profile" :component profile/ProfilePage}]
           [Route {:path "/project/:id" :component (fn [props] (let [params (useParams)
                                                                     ident [:project/id (:id params)]]
                                                                 #jsx [pr/ui-project-report ident]))}]
           [Route {:path "/user/:id" :component (fn [props] (let [params (useParams)
                                                                  ident [:user/id (:id params)]]
                                                              #jsx [up/ui-user-profile ident]))
                   :load load-user}]
           [Route {:path "/tb" :component tbp/TransactionBuilderPage}]
           [Route {:path "/search" :component sp/SearchPage}]
           [Route {:path "/wizards/new-project/:id"
                   :component WizardNewProject
                   :load (fn [params location] (t/add! ctx {:project/id (:id params)} {:replace [:component/id :project-wizard :project]}))}
            [Route {:path "/"
                    :component (fn [props] (let [params (useParams)
                                                 ident [:project/id (:id params)]]
                                             #jsx [istep/ui-basic-info-step ident]))}]
            [Route {:path "/contract"
                    :component (fn [props] (let [params (useParams)
                                                 ident [:project/id (:id params)]]
                                             #jsx [cstep/ui-contract-step ident]))}]]
           [Route {:path "/" :component home/HomePage}]]]))

(def default Root)
