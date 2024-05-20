(ns co-who.components.evm.function
  (:require ["solid-js" :refer [useContext createMemo Show onMount Index For]]
            ["../blueprint/button.jsx" :as b]
            ["./inputs.jsx" :as ein]
            #_[co-blue.icons.chevron-right :refer [chevron-right]]
            ["../../normad.mjs" :as n :refer [add]]
            ["../blueprint/label.jsx" :as l]
            ["../../Context.mjs" :refer [AppContext]]))

(defn function [ident
                {:local/keys [on-change open?] :or {open? true} :as local}]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        query [:function/id :name
               {:inputs [[:internalType :type :name :value]]}
               {:outputs [[:internalType :type :name :value]]}
               :stateMutability :type]
        data (createMemo #(n/pull store (get-in store ident) query))]
    #jsx [:div {}
          [:span {:class "flex inline-flex w-full items-center pb-2 dark:border-gray-600"}
           #_(b/icon-button {:class "dark:text-gray-600"} "cr " #_(chevron-right))
           [:h1 {:class "font-bold dark:border-gray-600 border-gray-200 "}
            (str (:function/id (data))  ": ")
            (str (:name (data)))]]
          #jsx [Show {:when open?}
                [:div {}
                 #jsx [Show {:when (not (empty? (:inputs (data))))}
                       [:h2 {:class "mb-2 font-bold"} (str "Inputs: ")]
                       #jsx [For {:each (vec (:inputs (data)))}
                             (fn [entry i]
                               (ein/input entry {:local/editable? true
                                                 :local/on-change (fn [e]
                                                                    (ein/set-abi-field ctx [:function/id (:function/id (data)) :inputs (i) :value] e.target.value))}))]]
                 #jsx [Show {:when (not (empty? (:outputs (data))))}
                       [:h2 {:class "mb-2 font-bold text-green"} (str "Outputs: ")]
                       #jsx [For {:each (vec (:outputs (data)))}
                             (fn [entry _]
                               (ein/input entry {:local/editable? false}))]]]]]))

#_(defn abi-entry [ident & local]
  (condp = (:type data)
    "function" (function data local)
    "constructor" ""
    "error" ""
    "event" ""
    :cljs.spec.alpha/invalid "inv"
    (str (:type data))))
