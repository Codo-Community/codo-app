#!/usr/bin/env sh

ceramic daemon --network inmemory

. .ceramic_dev_key.env

# generate DID from private-key
composedb did:from-private-key ${DID_PRIVATE_KEY}

# did:key:z6MkkrEXvfk4n8PP2Xk9MM45yvgZumU52ChuKSmWEw5AgWkD

# build composeite from graphql schema
composedb composite:create SimpleProfile.graphql --output=simple-profile.json --did-private-key=${DID_PRIVATE_KEY}

composedb composite:deploy simple-profile.json --ceramic-url=http://localhost:7007 --did-private-key=${DID_PRIVATE_KEY}

composedb composite:compile my-first-composite.json runtime-composite.json

composedb graphql:server --ceramic-url=http://localhost:7007 --graphiql runtime-composite.json --did-private-key=${DID_PRIVATE_KEY} --port=5005
