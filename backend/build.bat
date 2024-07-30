@echo off

cd %~dp0//application

./gradlew clean build -x test