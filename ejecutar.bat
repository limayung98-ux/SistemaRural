@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo Iniciando SistemaRural-PE...
mvn exec:java
pause
