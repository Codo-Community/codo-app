(ns main.router
  (:require ["./comp.cljs" :as comp]
            ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["@solidjs/router" :refer [HashRouter Route useParams cache useNavigate]]
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
  (let [ctx (useContext AppContext)]
    (pr/load-project ctx [:project/id (:id params)]))
  #_(get-project [:project/id (:id params)]))

(defn create-project [{:keys [params location]}]
  (let [ctx (useContext AppContext)]
    (t/add! ctx {:project/id (:id params)} {:replace [:component/id :project-wizard :project] :check-session? false}))
  #_(create-p (:id params)))

#_(defn load-transactions)

(defc Router [this {:keys []}]
  #jsx [HashRouter {:root main/ui-main}
        [Route {:path "/projects" :component sp/ui-search-page
                :load (fn [{:keys [params location]}] (sp/load-projects sp/list-query))}]
        [Route {:path "/project/:id"}
         [Route {:path "/" :component (fn [props] (let [params (useParams)
                                                        ident [:project/id (:id params)]]
                                                    #jsx [pr/ui-project-report {:& {:ident ident}}]))
                 :load load-project}]
         #_[Route {:path "/transaction-builder" :component pr/Planner}]]
        [Route {:path "/transaction-builder" :component tbp/TransactionBuilderPage}]
        [Route {:path "/user/:id"}
         [Route {:path "/" :component (fn [props] (let [params (useParams)
                                                        ident (fn [] [:user/id (:id params)])]
                                                    #jsx [up/ui-user-profile {:& {:ident ident}}]))
                 :load (fn [{:keys [params location]}] (up/load-user-profile ctx [:user/id (:id (useParams))]))}]
         [Route {:path "/projects" :component sp/ui-search-page
                 :load (fn [{:keys [params location]}] (sp/load-projects sp/my-projects))}]]
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
        [Route {:path "/" :component (fn [props] (let [navigate (useNavigate)]
                                                   (navigate (str "/project/" "kjzl6kcym7w8y9qvonv5n3irhpkvnkg4f0vxax6vnkzij2vrj7rj5lxhk30lb5s"))
                                                   #jsx [:div {}] #_home/HomePage))}]])

(def ui-router (comp/comp-factory Router AppContext))
