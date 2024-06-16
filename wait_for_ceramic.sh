#!/usr/bin/env sh
bash -c 'source ./ceramic.sh && while [[ "$(npx ceramic status > /dev/null  2>&1 && echo $?)" != "0" ]]; do echo "waiting for ceramic node" && sleep 1; done'
echo "OK: vite should be starting, browse to localhost:3000"
