(ns composites
  (:require ["fs" :refer [readFileSync]
             ["@ceramicnetwork/http-client" :refer [CeramicClient]]
             ["@composedb/devtools-node" :refer [createComposite readEncodedComposite writeEncodedComposite writeEncodedCompositeRuntime]]
             ["@composedb/devtools" :refer [Composite]]
             ["dids" :refer [DID]]
             ;["key-did-provider-ed25519" :refer [Ed25519Provider]]
             ["key-did-resolver" :refer [getResolver]]
             ;["uint8arrays/from-string" :refer [fromString]]
             ]))

(def dev-client (atom (CeramicClient. "http://localhost:7007")))

(def model-dir "../main/composedb/model/")

(def composites ["../main/composedb/model/project.graphql"
                 "../main/composedb/model/category.graphql"])

(defn ^:async write-composite [client file-path]
  (js/await (createComposite client file-path )))

(defn deploy-all [client composites]
  (mapv composites #(write-composite client) composites))

(defn read-file [path]
  (readFileSync path {:encoding "utf-8"}))

(defn replace-var [file composite]
  (let [id (first composite.modelIDs)]
    (.replace file "var" id)))

(defn create-composite []
  (Composite.create ))

const categorySchema = readFileSync("./composites/01-posts.graphql", {
                                                                      encoding: "utf-8",/
                                                                      }).replace("$PROFILE_ID", profileComposite.modelIDs[0]);

  const postsComposite = await Composite.create({
    ceramic,
    schema: postsSchema,
  });
