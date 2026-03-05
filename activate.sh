#!/bin/bash
# ====================================================================
# Activate Java 17 for Spring Boot Microservices Workspace
# Source this file in each terminal session before using Maven
# Usage: source activate.sh
# ====================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo ""
echo "===================================================================="
echo "  Activating Java 17 Environment"
echo "===================================================================="
echo ""

# Check if workspace-local Java exists
if [ -f "$SCRIPT_DIR/java/bin/java" ]; then
    export JAVA_HOME="$SCRIPT_DIR/java"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "[INFO] Using workspace Java 17 from: $JAVA_HOME"
else
    echo "[INFO] Using system Java"
fi

echo ""
echo "Java Version:"
java -version
echo ""
echo "Maven Version:"
mvn -version 2>/dev/null || echo "[WARNING] Maven not found. Please install from: https://maven.apache.org/download.cgi"
echo ""
echo "===================================================================="
echo "  Java Environment Ready!"
echo "===================================================================="
echo ""
echo "You can now run Maven commands in this terminal."
echo ""
echo "Example:"
echo "  cd 09-observability/demo-logging"
echo "  mvn spring-boot:run"
echo ""
echo "===================================================================="
echo ""
