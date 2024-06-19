(ns main.components.wizards.new-project.info-step
  (:require ["../../../comp.cljs" :as comp]
            ["../../blueprint/input.cljs" :as in]
            ["../../blueprint/textarea.cljs" :as ta]
            ["../../../utils.cljs" :as u]
            ["@solidjs/router" :refer [useNavigate useParams]]
            ["../../../transact.cljs" :as t]
            ["../../blueprint/button.cljs" :as b]
            ["../../../composedb/client.cljs" :as cli]
            ["../../../Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn category-mutation []
  (str "mutation CreateCategory($i: CreateCategoryInput!){
          createCategory(input: $i){
            document { id name }
 } }"))

(defn basic-project-mutation []
  (str "mutation CreateProject($i: CreateProjectInput!){
          createProject(input: $i){
            document { id name description start categoryID created}
 } }"))


(defn mutation-vars [{:project/keys [id name description start categoryID created] :as data}]
  {:i {:content {:name name :description description :start start :categoryID categoryID :created created}}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} {:user/keys [id firstName lastName] :as data} navigate]
  (fn [e]
    (if e (.preventDefault e))

    ;; add category
    (-> (.executeQuery (:compose @cli/client)
                       (category-mutation)
                       {:i {:content {:name "Root"}}})
        (.then (fn [response]
                 (println "response: cat:" response)
                 (let [res (-> response :data :createCategory :document)]
                   ;; add project
                   (-> (.executeQuery (:compose @cli/client)
                                      (basic-project-mutation)
                                      (mutation-vars (merge (data) {:project/categoryID (:id res) :project/created (.toLocaleDateString (js/Date.) "sv")})))
                       (.then (fn [response]
                                (println "response: " response)
                                (let [res (-> response :data :createProject :document)]
                                  (t/add! ctx (u/nsd res :project)
                                          {:replace [:component/id :project-wizard :project]})
                                  (navigate (str "/wizards/new-project/" (:id res)))))))))))))

(defc BasicInfoStep [this {:project/keys [id name description start created] :as data}]
  (let [navigate (useNavigate)
        params (useParams)]
    #jsx [:form {:class "flex flex-col min-w-96 gap-3"
                 :onSubmit (on-click-mutation ctx data navigate)}
          [:span {:class "flex  w-full gap-3"}
           [in/input {:label "Name"
                      :placeholder "Project Name"
                      :value name
                      :on-change #(comp/set! this :project/name %)}]
           [in/input {:label "Start"
                      :placeholder "Project Start Date"
                      :value start
                      :datepicker ""
                      :type "date"
                      :on-change #(comp/set! this :project/start %)}]]
          [ta/textarea {:title "Description"
                        :value description
                        :on-change #(comp/set! this :project/description %)}]
          [Show {:when (or (u/uuid? (:id params)) (nil? (:id params)))}
           [:span {:class "flex w-full gap-3"}
            [b/button {:title "Submit"}]]]]))

(def ui-basic-info-step (comp/comp-factory BasicInfoStep AppContext))
