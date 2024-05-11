(ns co-who.blueprint.input
  (:require
            ["./label.jsx" :as l]
            [co-who.utils :as u]
            [co-who.utils :as util]
            #_[co-blue.icons.clipboard :refer [clipboard]]
            ["./button.jsx" :as b]
            #_[co-who.mutations :as m]))

(defn on-change-fn [ident value setStore]
  (setStore (first ident)
            (fn [x]
              (update-in x [(second ident) :counter/value] value))))

(defn input [{:keys [id ident label placeholder on-submit copy? on-change on-click readonly? required? extra-class left-icon]
              :or {required? false extra-class "" copy? true readonly? false left-icon false label nil
                   on-click (fn [e] #_(util/copy-to-clipboard children))
                   }} children]
  (dom/div {:class (str "mb-3 w-full" extra-class)}
           (l/label label)
           (dom/span {:class "flex flex-row relative"}
                     (dom/div {:class (str  "w-7 h-7 absolute left-2 top-2 " (if-not left-icon "hidden"))} left-icon)
                     (dom/button {:class (str  "w-5 h-5 absolute right-2.5 top-3 hover:text-white text-gray-400 " (if-not copy? "hidden"))
                                  :on-click on-click} (clipboard))
                     (dom/input (u/drop-false {:class (str "truncate bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500 h-11
                                                            focus:border-blue-500 block w-full pr-10 dark:bg-black dark:border-gray-600 dark:placeholder-gray-400
                                                            dark:text-white dark:focus:ring-blue-500
                                                            dark:focus:border-blue-500
                                                            invalid:[&:not(:placeholder-shown):not(:focus)]:border-red-500 " (if left-icon "pl-11" "pl-3"))
                                               :placeholder placeholder
                                               :on-change on-change
                                               :on-submit on-submit
                                               :value children
                                               :readonly readonly?
                                               :required required?})))))

(defn number-input [{:keys [id label readonly? placeholder on-submit on-change readonly? required?] :or {required? false extra-class "" readonly? false}} & children]
  (dom/div {:class "mb-3"}
           (l/label label)
           (dom/input (u/drop-false
                       {:type "number"
                        :aria-describedby "helper-text-explanation"
                        :class "bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-md focus:ring-blue-500 focus:border-blue-500 block w-full h-11
                        p-3 dark:bg-black dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                        :placeholder placeholder
                        :on-change on-change
                        :on-submit on-submit
                        :value children
                        :readonly readonly?
                        :required required?}))))

(defn bool-input [{:keys [ident label readonly? placeholder on-submit on-change readonly? required?] :or {required? false extra-class "" readonly? false}} & children]
  (dom/div {:class "mb-3"}
    (l/label label)
    (dom/input (u/drop-false
                {:type "checkbox"
                 :value ""
                 :class "w-4 h-4 border border-gray-300 rounded bg-gray-50 focus:ring-3 focus:ring-blue-300 dark:bg-gray-700 dark:border-gray-600
                                        dark:focus:ring-blue-600 dark:ring-offset-gray-800 dark:focus:ring-offset-gray-800"
                 :on-change (or on-change (on-change-fn co-who.app/app ident))
                 :required required?
                 :readonly readonly?}))))
