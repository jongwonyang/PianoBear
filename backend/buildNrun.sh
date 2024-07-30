#!/bin/bash

dirpath=`dirname $0`

sh $dirpath/build.sh

#docker compose build

docker compose -p pianobear up -d --build
