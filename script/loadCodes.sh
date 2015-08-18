#!/usr/bin/env bash
REDIS_BIN=redis-cli
HOST=192.168.10.53
PORT=6380

CODES=$(cat ./codes.txt)
for code in ${CODES}
do
    ${REDIS_BIN} -h ${HOST} -p ${PORT} hset "codeList" "${code}" "1"
done
