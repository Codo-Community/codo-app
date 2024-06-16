(ns main.router
  (:require ["./comp.cljs" :as comp]
            ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["@solidjs/router" :refer [HashRouter Route useParams cache]]
            ["./main.cljs" :as main]
            ["./Context.cljs" :refer [AppContext]]
            ["./transact.cljs" :as t]

            ["./pages/tbpage.cljs" :as tbp]
            ["./pages/home.cljs" :as home]
            ["./pages/profile.cljs" :as profile]
            ["./pages/search.cljs" :as sp]
            ["./components/project_report.cljs" :as pr]
            ["./components/userprofile.cljs" :as up]
            ["./utils.cljs" :as u]
            ;["./imports.cljs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/main.cljs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/info_step.cljs" :as istep]
            ["./components/wizards/new_project/contract_step.cljs" :as cstep])
  (:require-macros [comp :refer [defc]]))

(def get-user (cache (fn [ident] (let [ctx (useContext AppContext)]
                                      (up/load-user-profile ctx ident)))))

(defn load-user [{:keys [params location]}]
  (get-user [:user (:id params)]))

(def get-project (cache (fn [ident] (let [ctx (useContext AppContext)]
                                      (pr/load-project ctx ident)))))

(def create-p (cache (fn [id] (let [ctx (useContext AppContext)
                                    f (if (u/uuid? id)
                                        (fn ^:async [] (js/Promise.resolve (t/add! ctx {:project/id id} {:replace [:component/id :project-wizard :project]})))
                                        #(pr/load-project ctx [:project/id id]))]
                                (f)))))

(defn load-project [{:keys [params location]}]
  (get-project [:project/id (:id params)]))

(defn create-project [{:keys [params location]}]
  (create-p (:id params)))

(defc Router [this {:keys []}]
  #jsx [HashRouter {:root main/ui-main}
        [Route {:path "/projects" :component (fn [props] (let [ident [:component/id :project-list]] #jsx [sp/ui-search-page {:& {:ident ident}}]))
                :load (fn [{:keys [params location]}] (sp/load-projects))}]
        [Route {:path "/project/:id" :component (fn [props] (let [params (useParams)
                                                                  ident (fn [] [:project/id (:id params)])]
                                                              #jsx [pr/ui-project-report {:& {:ident ident}}]))
                :load load-project}]
        [Route {:path "/transaction-builder" :component tbp/TransactionBuilderPage}]
        [Route {:path "/user/:id" :component (fn [props] (let [params (useParams)
                                                               ident (fn [] [:user/id (:id params)])]
                                                           #jsx [up/ui-user-profile {:& {:ident ident}}]))
                :load (fn [{:keys [params location]}] (up/load-user-profile ctx [:user/id (:id (useParams))]) ;load-user
                        )}]
        [Route {:path "/wizards/new-project/:id"
                :component WizardNewProject}
         [Route {:path "/"
                 :component  (fn [props] (let [params (useParams)
                                               ident (fn [] [:project/id (:id params)])]
                                           #jsx [istep/ui-basic-info-step {:& {:ident ident}}]))
                 :load create-project}]
         [Route {:path "/contract"
                 :component (fn [props] (let [params (useParams)
                                              ident (fn [] [:project/id (:id params)])]
                                          #jsx [cstep/ui-contract-step {:& {:ident ident}}]))
                 :load create-project}]]
        [Route {:path "/" :component home/HomePage}]])

(def ui-router (comp/comp-factory Router AppContext))
