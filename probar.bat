@echo off
chcp 65001 > nul
cd /d "%~dp0"
echo Ejecutando pruebas automatizadas JUnit 5...
mvn test
pause
