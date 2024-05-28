(ns dev
  (:require
    ["fs" :refer [readFileSync copyFile]]
    ["dotenv/config"]

    ["@ceramicnetwork/http-client" :refer [CeramicClient]]
    ["@composedb/devtools-node" :refer [createComposite readEncodedComposite writeEncodedComposite writeRuntimeDefinition mergeEncodedComposites writeEncodedCompositeRuntime]]
    ["@composedb/devtools" :refer [Composite]]
    ["dids" :refer [DID]]
    ["key-did-provider-ed25519" :refer [Ed25519Provider]]
    ["key-did-resolver" :refer [getResolver]]
    ["uint8arrays/from-string" :refer [fromString]]))

(def ceramic (CeramicClient. "http://localhost:7007"))

(defn authenticate [ceramic]
  (let [seed (-> js/process :env :DID_PRIVATE_KEY)
        key (fromString seed "base16")
        did (DID. {:resolver (getResolver) :provider (Ed25519Provider. key)})]
    (-> (.authenticate did)
        (.then #(aset ceramic "did" did)))))

(defn ^:async write-composite [ceramic]
  (let [categoryComposite (js-await (createComposite ceramic "./src/main/composedb/model/category.graphql"))

        category-id (aget (.-modelIDs categoryComposite) 0)

        project-schema (.replace (readFileSync "./src/main/composedb/model/project.graphql"  {:encoding "utf-8"}) "$CATEGORY_ID" category-id)
        projectComposite (js-await (Composite.create  {:ceramic ceramic
                                                       :schema project-schema}))

        userComposite (js-await (createComposite ceramic "./src/main/composedb/model/user.graphql"))

        ;; asd (js-await (writeEncodedComposite categoryComposite "./dist/__generated__/composite_category.json"))
        ;; asd (js-await (writeEncodedComposite projectComposite "./dist/__generated__/composite_project.json"))
        ;; asd (js-await (writeEncodedComposite userComposite "./dist/__generated__/composite_user.json"))

        composite (js-await (Composite.from #js [categoryComposite projectComposite userComposite]))

        asd (js-await (writeEncodedComposite composite "./dist/__generated__/definition.json"))
        merged (js-await (writeEncodedCompositeRuntime ceramic "./src/__generated__/definition.json" "./src/__generated__/definition.js"))

        ;; merged (js-await (mergeEncodedComposites ceramic #js ["./dist/__generated__/composite_category.json"
        ;;                                                       "./dist/__generated__/composite_project.json"
        ;;                                                       "./dist/__generated__/composite_user.json"] "./src/__generated__/definition.json"))

        merged (js-await (readEncodedComposite ceramic "./src/__generated__/definition.json"))
        ;; asd (js-await (writeEncodedComposite merged "./src/__generated__/definition-enc.json"))
        ;; new (js-await (writeRuntimeDefinition merged "./resources/definition.js"))
        ;; new (js-await (writeRuntimeDefinition merged "./resources/definition.json"))
        ]
    (copyFile "./src/__generated__/definition.js" "./resources/definition.mjs" (fn [err] (js/console.log "copy to resources failed")))
    (js-await (merged.startIndexingOn ceramic))))

#_(.then (authenticate ceramic) (fn [r]
                                (println ceramic)
                                (.then (write-composite ceramic) (fn [r] r))))

(def default write-composite)
