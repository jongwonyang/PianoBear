@echo off

call %~dp0/frontend/build.bat

call %~dp0/backend/buildNrun.bat