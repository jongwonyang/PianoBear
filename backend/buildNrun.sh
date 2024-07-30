#!/bin/bash

dirpath=`dirname $0`

cd $dirpath

sh ./build.sh

#docker compose build

docker compose -p pianobear up -d --build
