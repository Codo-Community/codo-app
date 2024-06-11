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
            #_["./imports.cljs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/info_step.cljs" :as istep]
            ["./components/wizards/new_project/contract_step.cljs" :as cstep]
            ["./normad.cljs" :as norm]
            ["./composedb/client.cljs" :as cdb]
            ["./composedb/auth.cljs" :as cda]
            ["./composedb/composite.cljs" :as composite]
            ["@solidjs/router" :refer [HashRouter Route useParams cache]]
            ["flowbite-datepicker" :as dp]
            ["./utils.cljs" :as utils]
            ["./main.cljs" :as main]
            ["./comp.cljs" :as comp :refer [Comp]]
            ["./Context.cljs" :refer [AppContext]]
            ["./transact.cljs" :as t]
            ["./test.cljs" :as test]
            ["flowbite" :as fb]
            ["./geql.cljs" :as geql]
            [squint.string :as string])
  (:require-macros [comp :refer [defc]]))

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
  (create-p (:id params)))

(defn Root []
  (let [[store setStore] (createStore {:component/id {:header {:component/id :header
                                                               :user {:user/id 0
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
                ;; TODO: fetch abi here
                (.then (cda/init-auth)
                       (fn [r] (println r) (.then (cdb/init-clients)
                                                  (fn []))))))
    #jsx [AppContext.Provider {:value ctx}
          [HashRouter {:root main/Main}
           [Route {:path "/project/:id" :component (fn [props] (let [params (useParams)
                                                                     ident (fn [] [:project/id (:id params)])]
                                                                 #jsx [pr/ui-project-report {:& {:ident ident}}]))}
            [Route {:path "/" :component (fn [props] (let [params (useParams)
                                                           ident (fn [] [:project/id (:id params)])]
                                                       #jsx [pr/ui-project-planner {:& {:ident ident}}]))
                    :load load-project}]
            [Route {:path "/transaction-builder" :component tbp/TransactionBuilderPage}]]
           [Route {:path "/user/:id" :component (fn [props] (let [params (useParams)
                                                                  ident (fn [] [:user/id (:id params)])]
                                                              #jsx [up/ui-user-profile {:& {:ident ident}}]))
                   :load (fn [{:keys [params location]}] (up/load-user-profile ctx [:user/id (:id (useParams))]) ;load-user
                           )}]
           [Route {:path "/search" :component sp/SearchPage}]
           [Route {:path "/wizards/new-project/:id"
                   :component WizardNewProject}
            [Route {:path "/"
                    :component  (fn [props] (let [params (useParams)
                                                  ident (fn [] [:project/id (:id params)])]
                                              #jsx [istep/ui-basic-info-step {:& {:ident ident}}]))
                    load create-project}]
            [Route {:path "/contract"
                    :component (fn [props] (let [params (useParams)
                                                 ident (fn [] [:project/id (:id params)])]
                                             #jsx [cstep/ui-contract-step {:& {:ident ident}}]))}]]
           [Route {:path "/" :component home/HomePage}]]]))
