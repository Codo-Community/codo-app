(ns App
  (:require #_["./App.module.css$default" :as styles]
            #_["./logo.svg$default" :as logo]

            ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index]]

            #_["@thisbeyond/solid-dnd" :refer [DragDropProvider
                                               DragDropSensors
                                               useDragDropContext
                                               createDraggable
                                               createDroppable]]
            [squint.string :as str]
            ["solid-js/store" :refer [createStore]]
            ["./components/user.jsx" :as user]
            ["./utils.mjs" :as u]
            #_["datascript" :as d]))

(def AppContext (createContext))

#_(defn Draggable [{:keys [id]}]        ;
    (let [draggable (createDraggable id)]
      #jsx [:div {:id id
                  :use:draggable4 true
                                        ;:classList {"!droppable-accept" droppable}
                                        ;:class "draggable"
                  }
            #jsx draggable
            [:div {} "hi"]])
    )

#_(defn Droppable [{:keys [id]} & children]
  (let [droppable (createDroppable id)]
    #jsx [:div {:id id
                :use:droppable9 true}
          [:div {} "go"]
          #jsx droppable])
  )

(defn inc-counter [{:keys [store setStore] :as ctx} id]
  (setStore :counter/id
              (fn [counters]
                (update-in counters [id :counter/value] inc))))

(defn update-fn [{:keys [store setStore] :as ctx} ident f]
  (setStore (first ident)
              (fn [thing]
                (update thing (second ident) f))))

(defn Counter [ident]
  (let [ctx (useContext AppContext)
        {:keys [store setStore]} ctx
        data (if ident (createMemo (fn []
                                     (get-in store ident.children))) (fn [] {:counter/value 0
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


#_(def data {:counters [{:counter/id 0 :counter/value {:value/name "a-value"}}]})

#_(def normalized-db (u/normalize data))

#_(println "ndb: " normalized-db)

(defn Header [])

(defn App []
  (let [                           ;[_ {:keys [onDragEnd]}] (useDragDropContext)

                                        ;onDragEnd (fn [{{draggable :draggable droppable :droppable} :keys}] (if droppable (println "hi")))

        [store setStore] (createStore {:counters [[:counter/id 0]]
                                       :header {:user [:user/id 0]}
                                       :user/id {0 {:user/id 0
                                                    :user/ethereum-address "0x1"}}
                                       :counter/id {0 {:counter/id 0
                                                       :counter/value 1}}})
        ctx {:store store
             :setStore setStore}
        data (createMemo (fn []
                           (println "called memo fun: ")
                           (select-keys store [:counters :header])))]
    #jsx [:div {:class "flex h-screen w-screen flex-col overflow-hidden dark"}
          #_[:header {:class styles.header}
             #_[:img {:src logo
                      :class styles.logo
                      :alt "logo"}]
             #_[DragDropProvider
                [DragDropSensors
                 [Draggable {:id "draggable-1"}]
                 [Droppable {:id "droppable-1"}]]]
                                        ;[Draggable]
             ]
          [:header
           [:nav {:class "border-gray-200 text-gray-900 px-4
                          bg-[#f3f4f6] dark:bg-black select-none overflow-hidden
                          dark:border-gray-700 dark:text-gray-400"}
            [:div {:class "flex flex-wrap justify-between items-center mx-auto overflow-hidden"}
             [:span {:class "flex gap-3"}
              [:a {:draggable "false"
                   :href "#", :class "flex items-center"} "codo"]]]
            [:div {:class "flex items-center md:order-2 md:space-x-5 overflow-hidden py-2"}
             #jsx [user/User ctx (get-in (data) [:header :user])]]]]
          [:button {:class "mb-4"
                    :onclick #(add-counter ctx)} "add counter" #_(str "data: " (-> (data) :counters (first) :counter/value))]
          [AppContext.Provider {:value ctx}
           [Counter (first (:counters (data)))]
           [For {:each (:counters (data))}
            (fn [c _]
              #jsx [Counter c])]]]))

(def default App)
