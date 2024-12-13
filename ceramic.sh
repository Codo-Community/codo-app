#!/usr/bin/env sh
. ./.env.development

comp_path=./src/main/composedb/model/
out_path=./dist/

ceramic_daemon () {
    ~/Downloads/ceramic-one_x86_64-unknown-linux-gnu.bin/ceramic-one daemon --network dev-unstable --store-dir ~/.ceramic-one --p2p-key-dir ~/.ceramic-one
    #podman-compose down; podman-compose up
}

did_from_private_key() {
    composedb did:from-private-key ${DID_PRIVATE_KEY}
}

# build composeite from graphql schema
create_from_schema () {
    local graphql_file=$1
    local basen=""${graphql_file##*/}""
    local out_file=composite_"${basen%.*}".json
    composedb composite:create ${graphql_file} --output=./dist/${out_file} --did-private-key=${DID_PRIVATE_KEY}
}

testa() {
    dir="${1}"
    for file in "${dir}/*.graphql"; do
        echo $file
    done
}


merge_composites() {
    composedb composite:merge dist/__generated__/composite_project.json dist/__generated__/composite_user.json dist/__generated__/composite_category.json   > dist/merged.json
}

compile_composite() {
    composedb composite:compile dist/merged.json dist/runtime-composite.json
}

deploy_composite() {
    composedb composite:deploy simple-profile.json --ceramic-url=http://localhost:7007 --did-private-key=${DID_PRIVATE_KEY}
}

graphql_server() {
     composedb graphql:server --ceramic-url=${VITE_CERAMIC_API} --graphiql src/__generated__/merged-rt.json --did-private-key=${DID_PRIVATE_KEY} --port=5005
}
