(ns App
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index]]
            [squint.string :as str]
            ["solid-js/store" :refer [createStore]]
            ["./components/user.jsx" :as user]
            ["./components/evm/transaction_builder.jsx" :as tb]
            ["./evm/util.mjs" :as eu]
            ["./normad.mjs" :as norm]
            ["./utils.mjs" :as u]
            ["@solidjs/router" :refer [HashRouter Route]]
            ["./Context.mjs" :refer [AppContext]]))

(defn inc-counter [{:keys [store setStore] :as ctx} id]
  (setStore :counter/id
            (fn [counters]
              (update-in counters [id :counter/value] inc))))

(defn update-fn [{:keys [store setStore] :as ctx} ident f]
  (setStore (first ident)
              (fn [thing]
                (update thing (second ident) f))))

(defn Counter [ident]
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (if ident
               (createMemo (fn []
                             (get-in store ident.children)))
               (fn [] {:counter/value 1
                       :counter/id -1}))]
    #jsx [:div
          "Count:" (str/join " " (range (:counter/value (data))))
          [:div
           [:button
            {:onClick #(inc-counter ctx (:counter/id (data)))}
            "Click me"]]]))

(defn add-counter [{:keys [store setStore] :as ctx}]
  (let [id (count (:counters store))]
    (setStore :counter/id
              (fn [counter-id]
                (assoc-in counter-id [id] {:counter/id id :counter/value (rand-int 10)})))
    (setStore :counters (fn [counters] (conj counters [:counter/id id])))))

(defn Header []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)
        data (createMemo #(get store :header))
        user-fn (eu/add-accounts-changed #(user/add-user ctx (first %)))]
    #jsx [:header {}
          [:nav {:class "border-gray-200 text-gray-900 px-4
                    bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                    dark:border-gray-700 dark:text-gray-400"}
           [:div {:class "flex flex-wrap justify-between items-center mx-auto overflow-hidden"}
            [:span {:class "flex gap-3"}
             [:a {:draggable "false"
                  :href "#", :class "flex items-center"} "codo"]]
            [:div {:class "flex items-center md:order-2 md:space-x-5 overflow-hidden py-2"}
             #jsx [user/User (:user (data))]]]]]))

(defn Counters []
  (let [{:keys [store setStore] :as ctx} (useContext AppContext)

        data (createMemo #(select-keys store [:counters]))]
    #jsx [For {:each (:counters (data))}
          (fn [c _]
            #jsx [Counter c])]))

(defn Main [props]
  #jsx [:div {:class "flex h-screen w-screen flex-col overflow-hidden dark text-gray-900"}
        #jsx [Header]
        [:div {:class "flex h-full overflow-hidden dark:text-white bg-[#f3f4f6] dark:bg-[#101014] py-4"}
         props.children]])

(defn Root []
  (let [[store setStore] (createStore {:counters [{:counter/id 0
                                                   :counter/value 1}]
                                       :transaction-builder {:contracts [[:contract/id :codo] [:contract/id :codo-governor]]
                                                             :contract [:contract/id :codo]
                                                             :transactions []}
                                       :header {:user {:user/id 0
                                                       :user/ethereum-address "0x0"
                                                       :user/leg {:leg/id "left"}}}})
        {:keys [store setStore] :as ctx} (norm/add {:store store :setStore setStore})]
    #jsx [AppContext.Provider {:value ctx}
          [HashRouter {:root Main}
           [Route {:path "/counter" :component Counters}]
           [Route {:path "/tb" :component tb/TransactionBuilder}]]]))

(def default Root)
