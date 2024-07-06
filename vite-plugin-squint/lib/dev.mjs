import * as squint_core from 'squint-cljs/core.js';
import { readFileSync, copyFile } from 'fs';
import 'dotenv/config';
import { CeramicClient } from '@ceramicnetwork/http-client';
import { createComposite, readEncodedComposite, writeEncodedComposite, writeRuntimeDefinition, mergeEncodedComposites, writeEncodedCompositeRuntime } from '@composedb/devtools-node';
import { Composite } from '@composedb/devtools';
import { DID } from 'dids';
import { Ed25519Provider } from 'key-did-provider-ed25519';
import { getResolver } from 'key-did-resolver';
import { fromString } from 'uint8arrays/from-string';
var ceramic = (() => {
const url1 = (() => {
const or__24281__auto__2 = squint_core.get(squint_core.get(process, "env"), "CERAMIC_API");
if (squint_core.truth_(or__24281__auto__2)) {
return or__24281__auto__2;} else {
return "http://localhost:7007";}
})();
return new CeramicClient(url1);
})();
var authenticate = async function (ceramic) {
const key1 = squint_core.get(squint_core.get(process, "env"), "DID_PRIVATE_KEY");
const did_string2 = fromString(key1, "base16");
const did3 = new DID(({ "resolver": getResolver(), "provider": new Ed25519Provider(did_string2) }));
return did3.authenticate().then((function () {
return squint_core.aset(ceramic, "did", did3);
}));
};
var write_composite = async function (ceramic) {
const userComposite1 = (await createComposite(ceramic, "./src/main/composedb/model/user.graphql"));
const user_id2 = userComposite1.modelIDs[0];
const post_schema3 = readFileSync("./src/main/composedb/model/post.graphql", ({ "encoding": "utf-8" })).replace("$USER_ID", user_id2);
const postComposite4 = (await Composite.create(({ "ceramic": ceramic, "schema": post_schema3 })));
const post_id5 = postComposite4.modelIDs[1];
const proposal_schema6 = readFileSync("./src/main/composedb/model/proposal.graphql", ({ "encoding": "utf-8" })).replace("$USER_ID", user_id2).replace("$POST_ID", post_id5);
const proposalComposite7 = (await Composite.create(({ "ceramic": ceramic, "schema": proposal_schema6 })));
const proposal_id8 = proposalComposite7.modelIDs[2];
const category_schema9 = readFileSync("./src/main/composedb/model/category.graphql", ({ "encoding": "utf-8" })).replace("$PROPOSAL_ID", proposal_id8);
const categoryComposite10 = (await Composite.create(({ "ceramic": ceramic, "schema": category_schema9 })));
const category_id11 = categoryComposite10.modelIDs[1];
const contractComposite12 = (await createComposite(ceramic, "./src/main/composedb/model/contract.graphql"));
const contract_id13 = contractComposite12.modelIDs[0];
const project_schema14 = readFileSync("./src/main/composedb/model/project.graphql", ({ "encoding": "utf-8" })).replace("$CATEGORY_ID", category_id11).replace("$CONTRACT_ID", contract_id13);
const projectComposite15 = (await Composite.create(({ "ceramic": ceramic, "schema": project_schema14 })));
const composite16 = (await Composite.from([userComposite1, postComposite4, proposalComposite7, categoryComposite10, contractComposite12, projectComposite15]));
const asd17 = (await writeEncodedComposite(composite16, "./src/__generated__/definition.json"));
const merged18 = (await writeEncodedCompositeRuntime(ceramic, "./src/__generated__/definition.json", "./src/__generated__/definition.js"));
const merged19 = (await writeEncodedCompositeRuntime(ceramic, "./src/__generated__/definition.json", "./src/__generated__/merged-rt.json"));
const merged20 = (await readEncodedComposite(ceramic, "./src/__generated__/definition.json"));
const new$21 = (await writeRuntimeDefinition(merged20, "./src/__generated__/definition-merged.json"));
return (await merged20.startIndexingOn(ceramic));
};

export { ceramic, authenticate, write_composite }
