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

(def ceramic (let [url (or (-> js/process :env :VITE_CERAMIC_API) "http://localhost:7007")]
               (CeramicClient. url)))

(defn ^:async authenticate [ceramic]
  (let [key (-> js/process :env :DID_PRIVATE_KEY)
        did-string (fromString key "base16")
        did (DID. {:resolver (getResolver) :provider (Ed25519Provider. did-string)})]
    (-> (.authenticate did)
        (.then #(aset ceramic "did" did)))))

(defn ^:async write-composite [ceramic]
  (let [userComposite (js-await (createComposite ceramic "./src/main/composedb/model/user.graphql"))
        user-id (aget (.-modelIDs userComposite) 0)

        post-schema (.replace (readFileSync "./src/main/composedb/model/post.graphql"  {:encoding "utf-8"}) "$USER_ID" user-id)
        postComposite (js-await (Composite.create  {:ceramic ceramic
                                                    :schema post-schema}))
        post-id (aget (.-modelIDs postComposite) 1)

        proposal-schema (.replace (.replace (readFileSync "./src/main/composedb/model/proposal.graphql"  {:encoding "utf-8"}) "$USER_ID" user-id) "$POST_ID" post-id)
        proposalComposite (js-await (Composite.create  {:ceramic ceramic
                                                        :schema proposal-schema}))
        proposal-id (aget (.-modelIDs proposalComposite) 2)

        category-schema (.replace (readFileSync "./src/main/composedb/model/category.graphql"  {:encoding "utf-8"}) "$PROPOSAL_ID" proposal-id)
        categoryComposite (js-await (Composite.create  {:ceramic ceramic
                                                        :schema category-schema}))
        category-id (aget (.-modelIDs categoryComposite) 1)

        contractComposite (js-await (createComposite ceramic "./src/main/composedb/model/contract.graphql"))
        contract-id (aget (.-modelIDs contractComposite) 0)

        organization-schema (.replace (.replace (readFileSync "./src/main/composedb/model/organization.graphql"  {:encoding "utf-8"}) "$USER_ID" user-id) "$CONTRACT_ID" contract-id)
        organizationComposite (js-await (Composite.create  {:ceramic ceramic
                                                            :schema organization-schema}))

        project-schema (.replace (.replace (readFileSync "./src/main/composedb/model/project.graphql"  {:encoding "utf-8"}) "$CATEGORY_ID" category-id) "$CONTRACT_ID" contract-id)
        projectComposite (js-await (Composite.create  {:ceramic ceramic
                                                       :schema project-schema}))

        composite (js-await (Composite.from #js [userComposite postComposite proposalComposite categoryComposite contractComposite projectComposite organizationComposite]))

        asd (js-await (writeEncodedComposite composite "./src/__generated__/definition.json"))

        merged (js-await (writeEncodedCompositeRuntime ceramic "./src/__generated__/definition.json" "./src/__generated__/definition.js"))
        merged (js-await (writeEncodedCompositeRuntime ceramic "./src/__generated__/definition.json" "./src/__generated__/merged-rt.json"))

                                        ;merged (js-await (writeEncodedCompositeRuntime ceramic "./src/__generated__/definition.json" "./src/__generated__/definition2.json"))

        ;; merged (js-await (mergeEncodedComposites ceramic #js ["./dist/__generated__/composite_category.json"
        ;;                                                       "./dist/__generated__/composite_project.json"
        ;;                                                       "./dist/__generated__/composite_user.json"] "./src/__generated__/definition.json"))

        merged (js-await (readEncodedComposite ceramic "./src/__generated__/definition.json"))

        ;; asd (js-await (writeEncodedComposite merged "./src/__generated__/definition-enc.json"))
        ;; new (js-await (writeRuntimeDefinition merged "./resources/definition.js"))
        new (js-await (writeRuntimeDefinition merged "./src/__generated__/definition-merged.json"))
        ]
    #_(copyFile "./src/__generated__/definition.js" "./resources/definition.mjs" (fn [err] (js/console.log err)))
    (js-await (merged.startIndexingOn ceramic))))

(.then (authenticate ceramic)
       (.then (write-composite ceramic) (fn [r]
                                          (println "wrote composite -----------------")
                                          (println (readFileSync "./src/__generated__/definition.json" {:encoding "utf-8"})))))
