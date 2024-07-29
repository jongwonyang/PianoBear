@echo off

call %~dp0//build.bat

docker compose -p pianobear up