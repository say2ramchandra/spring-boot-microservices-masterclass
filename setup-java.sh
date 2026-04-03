#!/bin/bash
# ====================================================================
# Java 17+ Setup Script for Linux/Mac
# This script sets up Java 17 for this workspace only
# ====================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAVA_DIR="$SCRIPT_DIR/java"

echo ""
echo "===================================================================="
echo "  Java 17+ Setup for Spring Boot Microservices Masterclass"
echo "===================================================================="
echo ""

# Check if workspace-local Java already exists
if [ -f "$JAVA_DIR/bin/java" ]; then
    echo "[INFO] Found existing Java installation in workspace"
    export JAVA_HOME="$JAVA_DIR"
    export PATH="$JAVA_HOME/bin:$PATH"
    verify_java
    exit 0
fi

echo "[INFO] Checking for Java 17+ on your system..."
echo ""

# Check system Java version
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d'.' -f1)
    
    echo "Found Java: $JAVA_VERSION"
    
    if [ "$MAJOR_VERSION" -ge 17 ]; then
        echo "[SUCCESS] Java 17+ is already installed on your system"
        echo ""
        echo "You can use your system Java, or download a workspace-local copy."
        echo ""
        read -p "Do you want to download Java 17 to this workspace folder? (y/n) " -n 1 -r
        echo ""
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            use_system_java
            exit 0
        fi
    else
        echo "[WARNING] Found Java $MAJOR_VERSION, but Java 17+ is required"
        echo ""
    fi
else
    echo "[WARNING] Java not found on system PATH"
    echo ""
fi

# Download Java
echo ""
echo "===================================================================="
echo "  Downloading Java 17 (Portable - No Installation Required)"
echo "===================================================================="
echo ""
echo "This will download Eclipse Temurin JDK 17 to:"
echo "  $JAVA_DIR"
echo ""
echo "This will NOT affect your system Java installation."
echo ""

mkdir -p "$JAVA_DIR"

# Detect OS and architecture
OS=$(uname -s)
ARCH=$(uname -m)

if [ "$OS" == "Darwin" ]; then
    # macOS
    if [ "$ARCH" == "arm64" ]; then
        DOWNLOAD_URL="https://api.adoptium.net/v3/binary/latest/17/ga/mac/aarch64/jdk/hotspot/normal/eclipse"
    else
        DOWNLOAD_URL="https://api.adoptium.net/v3/binary/latest/17/ga/mac/x64/jdk/hotspot/normal/eclipse"
    fi
    FILE_EXT="tar.gz"
elif [ "$OS" == "Linux" ]; then
    # Linux
    DOWNLOAD_URL="https://api.adoptium.net/v3/binary/latest/17/ga/linux/x64/jdk/hotspot/normal/eclipse"
    FILE_EXT="tar.gz"
else
    echo "[ERROR] Unsupported operating system: $OS"
    echo "Please download Java 17 manually from: https://adoptium.net/temurin/releases/?version=17"
    exit 1
fi

echo "[INFO] Downloading Eclipse Temurin JDK 17..."
echo "URL: $DOWNLOAD_URL"
echo ""

DOWNLOAD_FILE="/tmp/jdk17.$FILE_EXT"

if command -v wget &> /dev/null; then
    wget -O "$DOWNLOAD_FILE" "$DOWNLOAD_URL"
elif command -v curl &> /dev/null; then
    curl -L -o "$DOWNLOAD_FILE" "$DOWNLOAD_URL"
else
    echo "[ERROR] Neither wget nor curl is available"
    echo "Please download Java 17 manually from:"
    echo "  https://adoptium.net/temurin/releases/?version=17"
    echo ""
    echo "Extract to: $JAVA_DIR"
    exit 1
fi

echo ""
echo "[INFO] Extracting Java..."
tar -xzf "$DOWNLOAD_FILE" -C "$JAVA_DIR" --strip-components=1
rm "$DOWNLOAD_FILE"

echo "[SUCCESS] Java 17 installed to workspace!"
echo ""

verify_java

function verify_java {
    echo ""
    echo "===================================================================="
    echo "  Verifying Java Installation"
    echo "===================================================================="
    echo ""
    
    if [ -f "$JAVA_DIR/bin/java" ]; then
        export JAVA_HOME="$JAVA_DIR"
        export PATH="$JAVA_HOME/bin:$PATH"
    fi
    
    java -version
    echo ""
    
    echo "[SUCCESS] Java is ready!"
    echo ""
    
    create_activate_script
}

function use_system_java {
    echo ""
    echo "[INFO] Using system Java installation"
    echo ""
    create_activate_script
}

function create_activate_script {
    echo ""
    echo "===================================================================="
    echo "  Creating Activation Scripts"
    echo "===================================================================="
    echo ""
    
    # Create activate.sh for this workspace
    cat > "$SCRIPT_DIR/activate.sh" << 'EOF'
#!/bin/bash
# Activate Java 17 for this workspace

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -f "$SCRIPT_DIR/java/bin/java" ]; then
    export JAVA_HOME="$SCRIPT_DIR/java"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "[INFO] Using workspace Java 17"
else
    echo "[INFO] Using system Java"
fi

echo ""
echo "Java Environment Activated:"
java -version
echo ""
echo "Maven projects in this workspace will use this Java version."
echo ""
EOF

    chmod +x "$SCRIPT_DIR/activate.sh"
    
    echo "[SUCCESS] Created activate.sh"
    echo ""
    echo "===================================================================="
    echo "  Setup Complete!"
    echo "===================================================================="
    echo ""
    echo "To use Java 17 in this workspace:"
    echo ""
    echo "  1. Run: source activate.sh"
    echo "  2. Or add to your shell profile:"
    echo "     echo 'source $SCRIPT_DIR/activate.sh' >> ~/.bashrc"
    echo ""
    echo "This will set JAVA_HOME and PATH for the current terminal session only."
    echo ""
    echo "To run demos:"
    echo "  source activate.sh"
    echo "  cd 01-core-java-fundamentals/01-collections-framework/demo-arraylist-basics"
    echo "  mvn clean compile exec:java"
    echo ""
    echo "===================================================================="
}

verify_java
