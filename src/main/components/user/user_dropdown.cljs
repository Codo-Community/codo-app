(ns main.components.user.user-dropdown
  (:require ["solid-js" :refer [onMount]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["../../evm/gc_passport.cljs" :as gcp]
            ["../blueprint/dropdown.cljs" :as dr]
            ["flowbite" :refer [initDropdowns]])
  (:require-macros [sqeave :refer [defc]]))

(defc UserDropdown [this {:user/keys [id name ethereum-address passport-score] :as data :or {id nil name "" passport-core 0}}]
  (do
    (onMount (fn [] (initDropdowns)))
    #jsx [dr/dropdown {:& {:id (:data-dropdown-id props)
                           :title (fn [] #jsx [:p {:class "flex flex-col gap-1"} [:text {} (name)] [:text {} (str "passport: " (passport-score))]])
                           :items (fn [] [{:value "My Projects" :href (str "/user/" (id) "/projects") :icon "i-tabler-stack"}
                                          {:value "Profile" :href (str "/user/" (id)) :icon "i-tabler-user-circle"}
                                          {:value "Submit Passport" :icon "i-tabler-user-scan"
                                           :on-change (fn [ ]
                                                        (.then (gcp/submit-passport-no-verify (ethereum-address))
                                                               (fn [data]
                                                                 (println "data: " data)
                                                                 (sqeave/add! ctx {:user/id (id)
                                                                              :user/passport-score score} {:after (fn [] (initDropdowns) (initTooltips))}))))}
                                          ])}}]))
