#!/bin/bash

dirpath=`dirname $0`

sh $dirpath/frontend/build.sh

sh $dirpath/backend/buildNrun.sh
