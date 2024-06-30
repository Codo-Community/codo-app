(ns main.components.user.user-dropdown
  (:require ["../../comp.cljs" :as comp]
            ["../../Context.cljs" :refer [AppContext]]
            ["../blueprint/dropdown.cljs" :as dr])
  (:require-macros [comp :refer [defc]]))

(defc UserDropDown [this {:user/keys [id name passport-score] :as data :or {id nil name "" passport-core 0}}]
  #jsx [dr/dropdown {:& {:id (:data-dropdown-id props)
                         :title (fn [] #jsx [:p {:class "flex flex-col gap-1"} [:text {} (name)] [:text {} (str "passport: " (passport-score))]])
                         :items (fn [] [{:value "My Projects" :href (str "/user/" (id) "/projects") :icon "i-tabler-stack"}
                                        {:value "Profile" :href (str "/user/" (id)) :icon "i-tabler-user-circle"}])}}])

(def ui-user-dropdown (comp/comp-factory UserDropDown AppContext))
