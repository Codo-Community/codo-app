(ns main.components.wizards.new-organization.info-step
  (:require ["@w3t-ab/sqeave" :as sqeave]
            ["../../blueprint/input.cljs" :as in]
            ["../../blueprint/textarea.cljs" :as ta]
            ["@solidjs/router" :refer [useNavigate useParams]]
            ["../../blueprint/button.cljs" :as b]
            ["../../../composedb/util.cljs" :as cu])
  (:require-macros [sqeave :refer [defc]]))

#_(defn category-mutation []
  (str "mutation CreateCategory($i: CreateCategoryInput!){
          createCategory(input: $i){
            document { id name created }
 } }"))

(defn basic-organization-mutation []
  (str "mutation CreateOrganization($i: CreateOrganizationInput!){
          createOrganization(input: $i){
            document { id name description start created}
 } }"))


(defn mutation-vars [{:organization/keys [id name description start categoryID created] :as data}]
  {:i {:id id
       :content {:name name :description description :start start :categoryID categoryID :created created}}})

(defn on-click-mutation [{:keys [store setStore] :as ctx} data navigate]
  (fn [e]
    (if e (.preventDefault e))
    ;; add organization
    (cu/execute-gql-mutation ctx (basic-organization-mutation)
                             (mutation-vars (merge (data) {:organization/categoryID (:category/id response) :organization/created (.toLocaleDateString (js/Date.) "sv")}))
                             (fn [response]
                               (println "response: " response)
                               (sqeave/add! ctx response
                                            {:replace [:component/id :organization-wizard :organization]})
                               (navigate (str "/wizards/new-organization/" (:organization/id response)))))))

(defc BasicInfoStep [this {:organization/keys [id name description start created]
                           :or {id (sqeave/uuid) name "" description "" start "" created (.toLocaleDateString (js/Date.) "sv")} :as data}]
  (let [navigate (useNavigate)
        params (useParams)]
    #jsx [:form {:class "flex flex-col gap-3"
                 :onSubmit (on-click-mutation ctx this.data navigate)}
          [:span {:class "flex  w-full gap-3"}
           [in/input {:label "Name"
                      :placeholder "Organization Name"
                      :value name
                      :on-change #(sqeave/set! this :organization/name %)}]
           [in/input {:label "Start"
                      :placeholder "Organization Start Date"
                      :value start
                      :datepicker ""
                      :type "date"
                      :on-change #(sqeave/set! this :organization/start %)}]]
          [ta/textarea {:title "Description"
                        :value description
                        :on-change #(sqeave/set! this :organization/description %)}]
          [Show {:when (or (sqeave/uuid? (:id params)) (nil? (:id params)))}
           [:span {:class "flex w-full gap-3"}
            [b/button {:title "Submit"}]]]]))
