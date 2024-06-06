(ns main.components.wizards.new-project.info-step
  (:require ["../../../comp.mjs" :as comp]
            ["../../blueprint/input.jsx" :as in]
            ["../../blueprint/textarea.jsx" :as ta]
            ["../../../utils.mjs" :as u]
            ["@solidjs/router" :refer [useNavigate]]
            ["../../../transact.mjs" :as t]
            ["../../blueprint/button.jsx" :as b]
            ["../../../composedb/client.mjs" :as cli]
            ["../../../Context.mjs" :refer [AppContext]])
  (:require-macros [comp :refer [defc]]))

(defn e->v [e]
  (-> e :target :value))

(defn category-mutation []
  (str "mutation CreateCategory($i: CreateCategoryInput!){
          createCategory(input: $i){
            document { id name }
 } }"))

(defn basic-project-mutation []
  (str "mutation CreateProject($i: CreateProjectInput!){
          createProject(input: $i){
            document { id name description start categoryID }
 } }"))

(defn mutation-vars [{:project/keys [id name description start categoryID] :as data}]
  {:i {:content {:name name :description description :start start :categoryID categoryID}}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} {:user/keys [id firstName lastName] :as data} navigate]
  (fn [e]
    (if e (.preventDefault e))

    ;; add category
    (-> (.executeQuery (:compose @cli/client)
                       (category-mutation)
                       {:i {:content {:name "Root"}}})
        (.then (fn [response]
                 (js/console.log response)
                 (let [res (-> response :data :createCategory :document)]
                   ;; add project
                   (-> (.executeQuery (:compose @cli/client)
                                      (basic-project-mutation)
                                      (mutation-vars (merge (data) {:project/categoryID (:id res)})))
                       (.then (fn [response]
                                (js/console.log response)
                                (let [res (-> response :data :createProject :document)]
                                  (t/add! ctx (u/nsd res :project)
                                          {:replace [:component/id :project-wizard :project]})
                                  (navigate (str "/wizards/new-project/" (:id res)))))))))))))

(defc BasicInfoStep [this {:project/keys [id name description start] :as data}]
  (let [navigate (useNavigate)]
    #jsx [:form {:class "flex flex-col min-w-96 gap-3"
                 :onSubmit (on-click-mutation ctx data navigate)}
          [:span {:class "flex w-full gap-3"}

           [in/input {:label "Name"
                      :placeholder "Project Name"
                      :value name
                      :on-change #(t/set-field! ctx (conj children.children :project/name) (e->v %))}]
           [in/input {:label "Start"
                      :placeholder "Project Start Date"
                      :value start
                      :datepicker ""
                      :type "date"
                      :on-change #(t/set-field! ctx (conj children.children :project/start) (e->v %))}]]
          [ta/textarea {:title "Description"
                        :value description
                        :on-change #(t/set-field! ctx (conj children.children :project/description) (e->v %))}]
          [:span {:class "flex w-full gap-3"}
           [b/button {:title "Submit"}]]]))

(def ui-basic-info-step (comp/comp-factory BasicInfoStep AppContext))
