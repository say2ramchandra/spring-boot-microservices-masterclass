@echo off
REM Activate Java 17 for this workspace

set "JAVA_HOME=C:\D\Skills\spring-boot-microservices-masterclass\java"
set "PATH=C:\D\Skills\spring-boot-microservices-masterclass\java\bin;%PATH%"

echo Java Environment Activated:
java -version
echo.
echo Maven projects in this workspace will use this Java version.
