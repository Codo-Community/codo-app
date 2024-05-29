(ns main.components.blueprint.stepper
  (:require ["solid-js" :refer [createSignal Show createContext useContext For createMemo Index onMount lazy]]
            ["solid-icons/hi" :refer [HiOutlineCheck HiOutlineClipboardDocumentList HiOutlineCube]]
            ["../../Context.mjs" :refer [AppContext]]
            ["../../comp.mjs" :as comp])
  (:require-macros [comp :refer [defc]]))

(def icon-map {:check HiOutlineCheck
               :clipboard-document-list HiOutlineClipboardDocumentList
               :cube HiOutlineCube})


(defc Step [this {:keys [id heading details icon completed? active? on-click] :or {heading "step"
                                                                                   details ""
                                                                                   icon ""
                                                                                   completed? false
                                                                                   active? false}}]
  #jsx (println  "d " (data))
  #_#jsx [:li {:class "mb-10 ml-6"}
          [Show {:when (fn [] completed?)
                 :fallback (fn [] #jsx [:span {:class "absolute flex items-center justify-center w-8 h-8
                                                     bg-gray-100 rounded-full -left-4 ring-4 ring-white
                                                     dark:ring-zinc-900 dark:bg-zinc-700"}
                                        [:div {:class (if active?
                                                        "w-5 h-5 text-black dark:text-white"
                                                        "w-5 h-5 text-gray-500 dark:text-gray-400")}
                                         [(icon icon-map)]]])}
           [:span {:class "absolute flex items-center justify-center w-8 h-8
                       bg-green-200 rounded-full -left-4 ring-4 ring-white
                       dark:bg-green-900"}
            [:div {:class "w-5 h-5 text-green-500 dark:text-green-400"}
             [HiOutlineCheck]]]]
          [:div {:class "hover:cursor-pointer"
                 :onClick on-click}
           [:h3 {:class (if active?
                          "font-medium leading-tight text-black dark:text-white"
                          "font-medium leading-tight")} heading]
           [:p {:class (if active?
                         "text-sm text-black dark:text-white"
                         "text-sm")} details]]])

(def ui-step (comp/comp-factory Step AppContext))
