@echo off
REM ====================================================================
REM Java 17+ Setup Script for Windows
REM This script sets up Java 17 for this workspace only
REM ====================================================================

echo.
echo ====================================================================
echo   Java 17+ Setup for Spring Boot Microservices Masterclass
echo ====================================================================
echo.

REM Check if workspace-local Java already exists
if exist "%~dp0java\bin\java.exe" (
    echo [INFO] Found existing Java installation in workspace
    set "JAVA_HOME=%~dp0java"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    goto :verify
)

echo [INFO] Checking for Java 17+ on your system...
echo.

REM Check system Java version
java -version 2>&1 | findstr /i "version" > nul
if %errorlevel% equ 0 (
    for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
        set JAVA_VERSION=%%g
    )
    echo Found Java: !JAVA_VERSION!
    
    REM Extract major version
    for /f "tokens=1 delims=." %%a in ("!JAVA_VERSION!") do set MAJOR_VERSION=%%a
    set MAJOR_VERSION=!MAJOR_VERSION:"=!
    
    if !MAJOR_VERSION! GEQ 17 (
        echo [SUCCESS] Java 17+ is already installed on your system
        echo.
        echo You can use your system Java, or download a workspace-local copy.
        echo.
        choice /C YN /M "Do you want to download Java 17 to this workspace folder (recommended)"
        if errorlevel 2 goto :use_system
        if errorlevel 1 goto :download
    ) else (
        echo [WARNING] Found Java !MAJOR_VERSION!, but Java 17+ is required
        echo.
        goto :download
    )
) else (
    echo [WARNING] Java not found on system PATH
    echo.
    goto :download
)

:download
echo.
echo ====================================================================
echo   Downloading Java 17 (Portable - No Installation Required)
echo ====================================================================
echo.
echo This will download Eclipse Temurin JDK 17 to:
echo   %~dp0java
echo.
echo This will NOT affect your system Java installation.
echo.
pause

mkdir "%~dp0java" 2>nul

echo [INFO] Downloading Eclipse Temurin JDK 17...
echo.
echo Please download Java 17 manually from:
echo   https://adoptium.net/temurin/releases/?version=17
echo.
echo Download Options:
echo   - Operating System: Windows
echo   - Architecture: x64
echo   - Package Type: JDK
echo   - Format: .zip (for portable installation)
echo.
echo Extract the downloaded ZIP to:
echo   %~dp0java
echo.
echo The structure should be:
echo   %~dp0java\bin\java.exe
echo   %~dp0java\lib\...
echo.
echo After extraction, run this script again.
echo.
pause
exit /b 1

:use_system
echo.
echo [INFO] Using system Java installation
echo.
goto :create_activate

:verify
echo.
echo ====================================================================
echo   Verifying Java Installation
echo ====================================================================
echo.

java -version
echo.

if %errorlevel% neq 0 (
    echo [ERROR] Java verification failed
    exit /b 1
)

echo [SUCCESS] Java is ready!
echo.

:create_activate
echo.
echo ====================================================================
echo   Creating Activation Scripts
echo ====================================================================
echo.

REM Create activate.bat for this workspace
(
echo @echo off
echo REM Activate Java 17 for this workspace
echo.
if exist "%~dp0java\bin\java.exe" (
    echo set "JAVA_HOME=%~dp0java"
    echo set "PATH=%~dp0java\bin;%%PATH%%"
) else (
    echo REM Using system Java
)
echo.
echo echo Java Environment Activated:
echo java -version
echo echo.
echo echo Maven projects in this workspace will use this Java version.
) > "%~dp0activate.bat"

echo [SUCCESS] Created activate.bat
echo.
echo ====================================================================
echo   Setup Complete!
echo ====================================================================
echo.
echo To use Java 17 in this workspace:
echo.
echo   1. Run: activate.bat
echo   2. Or open a new terminal and run: activate.bat
echo.
echo This will set JAVA_HOME and PATH for the current terminal session only.
echo.
echo To run demos:
echo   activate.bat
echo   cd 01-core-java-fundamentals\01-collections-framework\demo-arraylist-basics
echo   mvn clean compile exec:java
echo.
echo ====================================================================
pause
