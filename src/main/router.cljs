(ns main.router
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["@solidjs/router" :as r :refer [Route useParams cache useNavigate]]
            ["./main.cljs" :as main]
            ["./Context.cljs" :refer [AppContext]]

            ["./components/evm/transaction_builder.cljs" :as tbp]
            ["./pages/home.cljs" :as home]
            ["./pages/profile.cljs" :as profile]
            ["./pages/search.cljs" :as sp]

            ["./components/project_report.cljs" :as pr]
            ["./components/userprofile.cljs" :as up]

            ;["./imports.cljs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/main.cljs" :refer [WizardNewProject]]
            ["./components/wizards/new_project/info_step.cljs" :as istep]
            ["./components/wizards/new_project/contract_step.cljs" :as cstep]
            ["./components/wizards/new_org/main.cljs" :refer [WizardNewOrganization]]
            ["./components/wizards/new_org/info_step.cljs" :as orgistep]
            ["./components/wizards/new_org/business_step.cljs" :as orgbstep])
  (:require-macros [sqeave :refer [defc]]))

(def get-user (cache (fn [ident] (let [ctx (useContext AppContext)]
                                      (up/load-user-profile ctx ident)))))

(defn load-user [{:keys [params location]}]
  (get-user [:user (:id params)]))

(def get-project (cache (fn [ident] (let [ctx (useContext AppContext)]
                                      (pr/load-project ctx ident)))))

(def create-p (cache (fn [id] (let [ctx (useContext AppContext)
                                    f (if (sqeave/uuid? id)
                                        (fn ^:async [] (js/Promise.resolve (sqeave/add! ctx {:project/id id} {:replace [:component/id :project-wizard :project]})))
                                        #(pr/load-project ctx [:project/id id]))]
                                (f)))))

(defn load-project [{:keys [params location]}]
  (let [ctx (useContext AppContext)]
    (pr/load-project ctx [:project/id (:id params)]))
  #_(get-project [:project/id (:id params)]))

(defn create-project [{:keys [params location]}]
  (let [ctx (useContext AppContext)]
    (sqeave/add! ctx {:project/id (:id params)} {:replace [:component/id :project-wizard :project]}))
  #_(create-p (:id params)))

(defn create-organization [{:keys [params location]}]
  (let [ctx (useContext AppContext)]
    (sqeave/add! ctx (merge (orgistep/BasicInfoStepClass.new-data) {:organization/id (:id params)} ) {:replace [:component/id :organization-wizard :organization]}))
  #_(create-p (:id params)))

#_(defn load-transactions)

(defc Router [this {:keys []}]
  #jsx [r/Router {:root main/Main}
        [Route {:path "/projects" :component (fn [] #jsx [sp/SearchPage {:& {:ident [:component/id :search]}}])
                :load (fn [{:keys [params location]}] (sp/load-projects sp/list-query))}]
        [Route {:path "/project/:id"}
         [Route {:path "/" :component (fn [props] (let [params (useParams)
                                                        ident [:project/id (:id params)]]
                                                    #jsx [pr/ProjectReport {:& {:ident ident}}]))
                 :load (fn [{:keys [params location]}] (let [params (useParams)
                                                             ident [:project/id (:id params)]]
                                                         (println ident)
                                                         (pr/load-project ctx ident)))}]]
        [Route {:path "/transaction-builder" :component (fn [] #jsx [tbp/TransactionBuilder {:& {:ident [:component/id :transaction-builder]}}])}]
        [Route {:path "/user/:id"}
         [Route {:path "/" :component (fn [props] (let [params (useParams)
                                                        ident (fn [] [:user/id (:id params)])]
                                                    #jsx [up/UserProfile {:& {:ident ident}}]))
                 :load (fn [{:keys [params location]}] (up/load-user-profile ctx [:user/id (:id (useParams))]))}]
         [Route {:path "/projects" :component (fn [] #jsx [sp/SearchPage {:& {:ident [:component/id :search]}}])
                 :load (fn [{:keys [params location]}] (sp/load-projects sp/my-projects))}]]
        [Route {:path "/wizards/new-organization/:id"
                :component (fn [props] #jsx [WizardNewOrganization {:& {:ident [:component/id :org-wizard]}}
                                             props.children])}
         [Route {:path "/"
                 :component  (fn [props] (let [params (useParams)
                                               ident (fn [] [:organization/id (:id params)])]
                                           #jsx [orgistep/BasicInfoStep {:& {:ident ident}}]))
                 :load create-organization}]
         [Route {:path "/business"
                 :component  (fn [props] (let [params (useParams)
                                               ident (fn [] [:organization/id (:id params)])]
                                           #jsx [orgistep/BasicInfoStep {:& {:ident ident}}]))
                 :load create-organization}]]
        [Route {:path "/wizards/new-project/:id"
                :component (fn [props] #jsx [WizardNewProject {:& {:ident [:component/id :project-wizard]}}
                                             props.children])}
         [Route {:path "/"
                 :component  (fn [props] (let [params (useParams)
                                               ident (fn [] [:project/id (:id params)])]
                                           #jsx [istep/BasicInfoStep {:& {:ident ident}}]))
                 :load create-project}]
         [Route {:path "/contract"
                 :component (fn [props] (let [params (useParams)
                                              ident (fn [] [:project/id (:id params)])]
                                          #jsx [cstep/ContractStep {:& {:ident ident}}]))
                 :load create-project}]]
        [Route {:path "/" :component (fn [props] (let [navigate (useNavigate)]
                                                   #_(navigate (str "/project/" "kjzl6kcym7w8y7xexray2dfec9f7jary378k7ynrvxm3m2vgqp0pew3zj4gk4pe"))
                                                   #jsx [home/HomePage]))}]])
