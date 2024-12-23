(ns co-who.blueprint.input
  (:require ["solid-js" :refer [Show createSignal onMount]]
            #_["flowbite-datepicker" :as dp]
            ["./label.cljs" :as l]))

(defn on-change-fn [ident value setStore]
  (setStore (first ident)
            (fn [x]
              (update-in x [(second ident) :counter/value] value))))

(defn input [{:keys [id ident label placeholder on-submit value copy on-change on-click readonly required extra-class left-icon value datepicker type ref on-focus-out]
              :as data
              :or {required false extra-class "" copy true readonly false left-icon false label nil datepicker nil type "text" ref false
                   value (fn [] "")
                   ;on-click (fn [e] (sqeave/copy-to-clipboard e.children))
                   }}]
  (let [datepicker-el (atom nil)
        in-el (js/crypto.randomUUID)]
    #_(if (= datepicker "")
        (onMount (fn [] (reset! datepicker-el (dp/Datepicker. ref {:todayBtn true :clearBtn true :language "en"
                                                                   :theme {}
                                                                   :container "dp"
                                                                   :autohide true})))))

    #jsx [:div {:id id
                :class (str "w-full" extra-class)}
          [l/label {:title label}]
          [:span {:class "flex flex-row relative"}
           [Show {:when (if left-icon true)}
            [:div {:class (str  "w-7 h-7 absolute left-2 sqeave/op-2 ")}
             (left-icon value)]
            [Show {:when copy}
             [:button {:class (str  "w-5 h-5 absolute right-2 top-2 hover:text-white text-gray-400 i-tabler-clipboard")
                       :data-copy-to-clipboard-target in-el}]]]
           [:input {:id in-el
                    :class (str "truncate bg-gray-50 border border-zinc-400 text-gray-900 text-sm rounded-md focus:ring-blue-500 h-10
                                                            focus:border-blue-500 block w-full pr-8 dark:bg-black dark:border-zinc-400 dark:placeholder-gray-400
                                                            dark:text-white dark:focus:ring-blue-500
                                                            dark:focus:border-blue-500
                                                            invalid:[&:not(:placeholder-shown):not(:focus)]:border-red-500 " (if left-icon "pl-12" "pl-3"))
                    ;:datepicker datepicker
                    :placeholder placeholder
                    :type (if (or (nil? type) (= type "")) "text" type)
                    :onChange on-change
                    :onFocusOut on-focus-out
                    :onSubmit on-submit
                    ;:onClick (if datepicker (fn [e] (aget @datepicker-el "show")))
                    :value (or (value) "")
                    :readonly readonly
                    :required required}]]
          #_[:div {:id "dp" :class "absolute z-10"}]]))

(defn number-input [{:keys [id label readonly placeholder on-submit on-change required value] :or {required false extra-class "" readonly false value (fn [] 0)}}]
  #jsx [:div {:class ""}
        [l/label {:title label}]
        [:input {:type "number"
                                        ;:aria-describedby "helper-text-explanation"
                 :class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500 focus:border-blue-500 block w-full h-10
                                  p-3 dark:bg-black dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                 :placeholder placeholder
                 :onChange on-change
                 :onSubmit on-submit
                 :value (or (value) "")
                 :readonly readonly
                 :required required}]])

#_(def number-input (sqeave/comp-factory NumberInput AppContext))

(defn boolean-input [{:keys [ident label placeholder on-submit on-change readonly required value extra-class inline]
                      :or {required false extra-class "" readonly false value (fn [] "") on-change (fn [e]) inline false}}]
  #jsx [:div {:class (str extra-class " " (if inline " flex flex-row gap-2 items-center" ""))}
        [l/label {:title label}]
        [:input {:type "checkbox"
                 :checked (or (value) "")
                 :class "w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-blue-300 dark:bg-gray-700 dark:border-gray-600
                                  dark:focus:ring-blue-600 dark:ring-offset-gray-800 dark:focus:ring-offset-gray-800"
                 :onChange on-change
                 :required required
                 :readonly readonly}]])
