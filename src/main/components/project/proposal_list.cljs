(ns main.components.project.proposal-list
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount]]
            ["../../Context.cljs" :refer [AppContext]]
            ["../blueprint/dropdown.cljs" :as d]
            ["../blueprint/label.cljs" :as l]
            ["../blueprint/split.cljs" :as s]
            ["../blueprint/icons/web3.cljs" :as wi]
            ["../../transact.cljs" :as t]
            ["./proposal.cljs" :as p]
            ["../../comp.cljs" :as comp])
  (:require-macros [comp :refer [defc]]))


(defc ProposalList [this {:keys [project/id proposals] :as data :or {}}]
  #jsx [:div {:class "dark:(placeholder-gray-400 focus:ring-blue-500 border-gray-700) w-full px-4
                          text-md overflow-auto"}
          [:div {:class "position-relative overflow-y-auto overflow-x-hidden"}
           [Show {:when (not (empty? (proposals)))}
            [For {:each (proposals)}
             (fn [p _]
               #jsx [p/proposal {:& {:ident p}}])]]]])

(def ui-proposal-list (comp/comp-factory ProposalList AppContext))

