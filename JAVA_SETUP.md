# Java 17+ Setup Guide

> **Set up Java 17 for this workspace without affecting your system installation**

## 🎯 Overview

This project requires **Java 17 or higher**. This guide helps you set up Java 17+ for this workspace only, without disturbing your system-wide Java installation.

---

## 🚀 Quick Setup (Recommended)

### For Windows:

```cmd
# Run the setup script
setup-java.bat

# Activate Java in your terminal
activate.bat

# Verify
java -version
```

### For Linux/Mac:

```bash
# Make scripts executable
chmod +x setup-java.sh activate.sh

# Run the setup script
./setup-java.sh

# Activate Java in your terminal
source activate.sh

# Verify
java -version
```

---

## 📋 What the Setup Script Does

1. **Checks** for Java 17+ on your system
2. **Downloads** Eclipse Temurin JDK 17 (portable version) to `./java/` folder
3. **Creates** activation scripts (`activate.bat` / `activate.sh`)
4. **Does NOT** modify your system environment variables
5. **Does NOT** require administrator privileges

---

## 🔧 Manual Setup (Alternative)

If you prefer to set up Java manually:

### Step 1: Download Java 17

Download Eclipse Temurin JDK 17 from:
**https://adoptium.net/temurin/releases/?version=17**

**Windows**:
- Operating System: Windows
- Architecture: x64
- Package Type: JDK
- Format: **.zip** (for portable installation)

**Linux/Mac**:
- Select your OS and architecture
- Package Type: JDK
- Format: **.tar.gz**

### Step 2: Extract to Workspace

Extract the downloaded file to:
```
spring-boot-microservices-masterclass/java/
```

The structure should be:
```
java/
├── bin/
│   ├── java.exe (Windows) or java (Linux/Mac)
│   └── ...
├── lib/
└── ...
```

### Step 3: Use Activation Scripts

Run `activate.bat` (Windows) or `source activate.sh` (Linux/Mac) in your terminal before running demos.

---

## 💻 Usage

### Every Time You Open a New Terminal:

**Windows:**
```cmd
cd path\to\spring-boot-microservices-masterclass
activate.bat
```

**Linux/Mac:**
```bash
cd path/to/spring-boot-microservices-masterclass
source activate.sh
```

### Then Run Demos:

```bash
# Example: Run logging demo
cd 09-observability/demo-logging
mvn spring-boot:run

# Example: Run metrics demo
cd 09-observability/demo-metrics-prometheus
mvn spring-boot:run
```

---

## 🔍 Verification

Check that Java 17+ is active:

```bash
# Check Java version
java -version

# Check JAVA_HOME
echo %JAVA_HOME%   # Windows
echo $JAVA_HOME    # Linux/Mac

# Check Maven can find Java 17
mvn -version
```

Expected output:
```
java version "17.0.x"
OpenJDK Runtime Environment Temurin-17...
```

---

## 📁 Workspace Structure

After setup, your workspace will look like:

```
spring-boot-microservices-masterclass/
├── java/                      # Java 17 (portable, workspace-local)
│   ├── bin/
│   ├── lib/
│   └── ...
├── setup-java.bat             # Setup script for Windows
├── setup-java.sh              # Setup script for Linux/Mac
├── activate.bat               # Activation script for Windows
├── activate.sh                # Activation script for Linux/Mac
├── JAVA_SETUP.md             # This file
├── 01-core-java-fundamentals/
├── 02-spring-core/
├── 09-observability/
└── ...
```

The `java/` folder is **local to this workspace** and does not affect your system.

---

## ❓ Troubleshooting

### Issue: "java: command not found"

**Solution:** You haven't activated the environment.
```bash
# Windows
activate.bat

# Linux/Mac
source activate.sh
```

### Issue: "Java version is wrong"

**Solution:** Make sure you activated the workspace environment:
```bash
# Check which Java is being used
where java      # Windows
which java      # Linux/Mac

# Should point to workspace java folder
```

### Issue: "Maven uses wrong Java version"

**Solution:** 
```bash
# Activate environment first
activate.bat  # or source activate.sh

# Then check
mvn -version

# Should show Java 17
```

### Issue: Setup script fails to download

**Solution:** Download Java manually:
1. Go to https://adoptium.net/temurin/releases/?version=17
2. Download .zip (Windows) or .tar.gz (Linux/Mac)
3. Extract to `./java/` folder
4. Run `activate.bat` or `source activate.sh`

---

## 🎯 Why Workspace-Local Java?

✅ **No system changes** - Your system Java remains untouched  
✅ **No admin required** - No installation needed  
✅ **Portable** - Can be moved or deleted easily  
✅ **Isolated** - Each project can have its own Java version  
✅ **Safe** - No conflicts with other projects  

---

## 🗑️ Uninstall

To remove Java 17 from this workspace:

**Windows:**
```cmd
rmdir /s /q java
del activate.bat
```

**Linux/Mac:**
```bash
rm -rf java
rm activate.sh
```

Your system Java is completely unaffected.

---

## 📚 Additional Resources

- **Eclipse Temurin**: https://adoptium.net/
- **Java 17 Features**: https://openjdk.org/projects/jdk/17/
- **Maven Download**: https://maven.apache.org/download.cgi

---

## ✅ Ready to Start!

Once Java 17 is set up:

1. ✅ Run `activate.bat` or `source activate.sh`
2. ✅ Navigate to any demo
3. ✅ Run `mvn spring-boot:run`
4. ✅ Start learning!

**Example:**
```bash
activate.bat
cd 09-observability\demo-logging
mvn spring-boot:run
```

---

**Happy Coding! ☕🚀**
