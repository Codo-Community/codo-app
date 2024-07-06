(ns main.components.user.user-dropdown
  (:require ["solid-js" :refer [onMount]]
            ["../../comp.cljs" :as comp]
            ["../../transact.cljs" :as t]
            ["../../evm/gc_passport.cljs" :as gcp]
            ["../../Context.cljs" :refer [AppContext]]
            ["../blueprint/dropdown.cljs" :as dr]
            ["flowbite" :refer [initDropdowns]])
  (:require-macros [comp :refer [defc]]))

(defc UserDropDown [this {:user/keys [id name ethereum-address passport-score] :as data :or {id nil name "" passport-core 0}}]
  (do
    (onMount (fn [] (initDropdowns)))
    #jsx [dr/dropdown {:& {:id (:data-dropdown-id props)
                           :title (fn [] #jsx [:p {:class "flex flex-col gap-1"} [:text {} (name)] [:text {} (str "passport: " (passport-score))]])
                           :items (fn [] [#_{:value "My Projects" :href (str "/user/" (id) "/projects") :icon "i-tabler-stack"}
                                          #_{:value "Profile" :href (str "/user/" (id)) :icon "i-tabler-user-circle"}
                                          #_{:value "Submit Passport" :icon "i-tabler-user-scan"
                                           :on-change (fn [ ]
                                                        (.then (gcp/submit-passport-no-verify (ethereum-address))
                                                               (fn [data]
                                                                 (println "data: " data)
                                                                 (t/add! ctx {:user/id (id)
                                                                              :user/passport-score score} {:after (fn [] (initDropdowns) (initTooltips))}))))}
                                          ])}}]))

(def ui-user-dropdown (comp/comp-factory UserDropDown AppContext))
