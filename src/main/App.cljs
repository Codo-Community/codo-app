(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["solid-js/store" :refer [createStore]]
            ["./pages/tbpage.cljs" :as tbp]
            ["./pages/home.cljs" :as home]
            ["./pages/profile.cljs" :as profile]
            ["./pages/search.cljs" :as sp]

            ["./components/blueprint/input.cljs" :as in]
            ["./components/project_report.cljs" :as pr]
            ["./components/userprofile.cljs" :as up]
            ["./components/wizards/new_project/main.cljs" :refer [WizardNewProject]]
            #_["./imports.mjs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/info_step.cljs" :as istep]
            ["./components/wizards/new_project/contract_step.cljs" :as cstep]
            ["./normad.cljs" :as norm]
            ["./composedb/client.cljs" :as cdb]
            ["./composedb/auth.cljs" :as cda]
            ["./composedb/composite.cljs" :as composite]
            ["@solidjs/router" :refer [HashRouter Route useParams cache]]
            ["flowbite-datepicker" :as dp]
            ["./utils.cljs" :as utils]
            ["./components/header.cljs" :as h]
            ["./comp.cljs" :as comp :refer [Comp]]
            ["./Context.cljs" :refer [AppContext]]
            ["./transact.cljs" :as t]
            ["flowbite" :as fb]
            ["./geql.cljs" :as geql]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

#_(defn import-proj [comp-path f] (.then (import comp-path) (fn [r] (println "got r " (:WizardNewProject r)) (get r f))))

#_(defn ^:async import-p [path] (println "importing " path) (import path))

#_(.then (import-p "./components/wizards/new_project/main.cljs") (fn [r]
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
  (let [header [:component/id :header]]
    #jsx [:div {:class "flex h-screen w-screen flex-col overflow-hidden text-gray-900 dark font-mono dark:text-white bg-[#f3f4f6] dark:bg-[#101014]"}
          [h/ui-header header]
          [:div {:class "flex h-screen w-sceen overflow-auto dark:text-white bg-[#f3f4f6] dark:bg-[#101014] justify-center"}
           #_(str (get-in js/window [:store :user/id "k2t6wzhkhabz1tk8f3pxf7gaecz67s8t1dezuxyfs6y6vamst77shwebvfpaz8"]))
           props.children]]))

(def get-user (cache (fn [ident] (let [ctx (useContext AppContext)]
                                      (up/load-user-profile ctx ident)))))

(defn load-user [{:keys [params location]}]
  (get-user [:user (:id params)]))

(def get-project (cache (fn [ident] (let [ctx (useContext AppContext)]
                                      (pr/load-project ctx ident)))))

(def create-p (cache (fn [id] (let [ctx (useContext AppContext)
                                    f (fn ^:async [] (js/Promise.resolve (t/add! ctx {:project/id id} {:replace [:component/id :project-wizard :project]})))]
                                (f)))))

(defn load-project [{:keys [params location]}]
  (get-project [:project (:id params)]))

(defn create-project [{:keys [params location]}]
  (println (:id params))
  (create-p (:id params)))

(defn Root []
  (let [[store setStore] (createStore {:component/id {:header {:user {:user/id 0
                                                                      :user/ethereum-address "0x0"}}
                                                      :project-wizard {:project []}
                                                      :project-list {:projects []}}
                                       :pages/id {:profile {:user {:user/id 0
                                                                   :user/ethereum-address "0x0"}}
                                                  :transaction-builder {:contracts []
                                                                        :contract nil
                                                                        :transactions []}}})
        {:keys [store setStore] :as ctx} (norm/add {:store store :setStore setStore})]
    (set! (.-store js/window) store)
    (onMount #(do
                (fb/initFlowbite)
                ;; fetch abi here
                (fn [] (.then (cda/init-auth)
                              (fn [] (.then (cdb/init-clients)
                                            (fn [])))))))
    #jsx [AppContext.Provider {:value ctx}
          [HashRouter {:root Main}
           [Route {:path "/project/:id" :component (fn [props] (let [params (useParams)
                                                                     ident [:project/id (:id params)]]
                                                                 #jsx [pr/ui-project-report ident]))}
            [Route {:path "/" :component (fn [props] (let [params (useParams)
                                                           ident [:project/id (:id params)]]
                                                       (println ident)
                                                       #jsx [pr/ui-project-planner ident]))
                    :load load-project}]
            [Route {:path "/transaction-builder" :component tbp/TransactionBuilderPage}]]
           [Route {:path "/user/:id" :component (fn [props] (let [params (useParams)
                                                                  a (println "user " (:id params))
                                                                  ident [:user/id (:id params)]]
                                                              #jsx [up/ui-user-profile ident]))
                   :load load-user}]
           [Route {:path "/search" :component sp/SearchPage}]
           [Route {:path "/wizards/new-project/:id"
                   :component WizardNewProject}
            [Route {:path "/"
                    :component (fn [props] (let [params (useParams)
                                                 ident [:project/id (:id params)]]
                                             #jsx [istep/ui-basic-info-step ident]))
                    :load create-project}]
            [Route {:path "/contract"
                    :component (fn [props] (let [params (useParams)
                                                 ident [:project/id (:id params)]]
                                             #jsx [cstep/ui-contract-step ident]))}]]
           [Route {:path "/" :component home/HomePage}]]]))

(def default Root)
