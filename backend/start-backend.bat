@echo off
echo Setting up Java environment...
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

echo Starting Reconciliation Engine Backend...
cd /d "%~dp0"
mvn spring-boot:run
