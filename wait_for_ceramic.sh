#!/usr/bin/env sh
bash -c 'source ./ceramic.sh && while [[ "$(curl -ivk localhost:5101 > /dev/null  2>&1 && echo $?)" != "0" ]]; do echo "waiting for ceramic-one node" && sleep 1; done'
pnpm dlx @ceramicnetwork/cli daemon --network dev-unstable &
bash -c 'source ./ceramic.sh && while [[ "$(pnpm dlx @ceramicnetwork/cli status > /dev/null  2>&1 && echo $?)" != "0" ]]; do echo "waiting for ceramic node" && sleep 1; done'
echo "OK: vite should be starting, browse to localhost:3000"
