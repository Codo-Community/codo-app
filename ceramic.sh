#!/usr/bin/env sh
. ./.ceramic_dev_key.env

comp_path=./src/composedb/model/
out_path=./dist/

ceramic_daemon () {
    ceramic daemon --network inmemory
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
    composedb composite:merge dist/composite_*.json > dist/merged.json
}

compile_composite() {
    composedb composite:compile dist/merged.json dist/runtime-composite.json
}

deploy_composite() {
    composedb composite:deploy simple-profile.json --ceramic-url=http://localhost:7007 --did-private-key=${DID_PRIVATE_KEY}
}

graphql_server() {
     composedb graphql:server --ceramic-url=http://localhost:7007 --graphiql dist/runtime-composite.json --did-private-key=${DID_PRIVATE_KEY} --port=5005
}
