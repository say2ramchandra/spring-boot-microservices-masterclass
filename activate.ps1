# ====================================================================
# Activate Java 17 for Spring Boot Microservices Workspace
# Usage: . .\activate.ps1  (note the dot space at the beginning!)
# ====================================================================

$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path
$JAVA_DIR = Join-Path $SCRIPT_DIR "java"

Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "  Activating Java 17 Environment" -ForegroundColor Cyan
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""

# Check if workspace-local Java exists
if (Test-Path "$JAVA_DIR\bin\java.exe") {
    $env:JAVA_HOME = $JAVA_DIR
    $env:PATH = "$JAVA_DIR\bin;$env:PATH"
    Write-Host "[INFO] Using workspace Java 17 from: $JAVA_HOME" -ForegroundColor Green
} else {
    Write-Host "[INFO] Using system Java" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Java Version:" -ForegroundColor Cyan
java -version
Write-Host ""
Write-Host "Maven Version:" -ForegroundColor Cyan
$mvnCheck = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvnCheck) {
    mvn -version
} else {
    Write-Host "[WARNING] Maven not found. Please install Maven from: https://maven.apache.org/download.cgi" -ForegroundColor Red
}
Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host "  Java Environment Ready!" -ForegroundColor Green
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "You can now run Maven commands in this terminal." -ForegroundColor White
Write-Host ""
Write-Host "Example:" -ForegroundColor Yellow
Write-Host "  cd 09-observability\demo-logging" -ForegroundColor White
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "====================================================================" -ForegroundColor Cyan
Write-Host ""
