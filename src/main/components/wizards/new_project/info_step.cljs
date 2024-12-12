(ns main.components.wizards.new-project.info-step
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["../../blueprint/input.cljs" :as in]
            ["../../blueprint/textarea.cljs" :as ta]
            ["@solidjs/router" :refer [useNavigate useParams]]
            ["../../blueprint/button.cljs" :as b]
            ["../../../composedb/util.cljs" :as cu])
  (:require-macros [sqeave :refer [defc]]))

(defn category-mutation []
  (str "mutation CreateCategory($i: CreateCategoryInput!){
          createCategory(input: $i){
            document { id name created }
 } }"))

(defn basic-project-mutation []
  (str "mutation CreateProject($i: CreateProjectInput!){
          createProject(input: $i){
            document { id name description start categoryID created}
 } }"))


(defn mutation-vars [{:project/keys [id name description start categoryID created] :as data}]
  {:name name :description description :start start :categoryID categoryID :created created})

(defn on-click-mutation [{:keys [store setStore] :as ctx} data navigate]
  (fn [e]
    (if e (.preventDefault e))

    ;; add category
    (cu/execute-gql-mutation ctx (category-mutation)
                             {:name "Root" :created (.toLocaleDateString (js/Date.) "sv")}
                             (fn [response]
                               (println "response: cat:" response)
                               ;; add project
                               (cu/execute-gql-mutation ctx (basic-project-mutation)
                                                        (mutation-vars (merge (data) {:project/categoryID (:category/id response) :project/created (.toLocaleDateString (js/Date.) "sv")}))
                                                        (fn [response]
                                                          (println "response: " response)
                                                          (sqeave/add! ctx response
                                                                  {:replace [:component/id :project-wizard :project]})
                                                          (navigate (str "/wizards/new-project/" (:project/id response)))))))))

(defc BasicInfoStep [this {:project/keys [id name description start created] :or {name ""} :as data}]
  (let [navigate (useNavigate)
        params (useParams)]
    #jsx [:form {:class "flex flex-col gap-3"
                 :onSubmit (on-click-mutation ctx this.data navigate)}
          [:span {:class "flex  w-full gap-3"}
           [in/input {:label "Name"
                      :placeholder "Project Name"
                      :value name
                      :on-change #(sqeave/set! this :project/name %)}]
           [in/input {:label "Start"
                      :placeholder "Project Start Date"
                      :value start
                      :datepicker ""
                      :type "date"
                      :on-change #(sqeave/set! this :project/start %)}]]
          [ta/textarea {:title "Description"
                        :value description
                        :on-change #(sqeave/set! this :project/description %)}]
          [Show {:when (or (sqeave/uuid? (:id params)) (nil? (:id params)))}
           [:span {:class "flex w-full gap-3"}
            [b/button {:title "Submit"}]]]]))
