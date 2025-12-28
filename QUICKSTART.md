# Quick Start Guide

> **Get up and running with the masterclass in 5 minutes!**

## 🎯 Choose Your Starting Point

### Scenario 1: Complete Beginner
**Start here**: Module 01 - Core Java Fundamentals

```bash
cd 01-core-java-fundamentals/01-collections-framework/demo-arraylist-basics
mvn clean compile
mvn exec:java
```

### Scenario 2: Know Java, New to Spring
**Start here**: Module 02 - Spring Core

```bash
cd 02-spring-core/01-dependency-injection/demo-constructor-injection
mvn clean compile
mvn exec:java
```

### Scenario 3: Know Spring, New to Spring Boot
**Start here**: Module 03 - Spring Boot Fundamentals

```bash
cd 03-spring-boot-fundamentals/04-rest-api-development/demo-complete-rest-api
mvn spring-boot:run
```

### Scenario 4: Ready for Microservices
**Start here**: Module 04 - Microservices Architecture

```bash
cd 04-microservices-architecture/01-service-discovery/demo-eureka-server
mvn spring-boot:run
```

---

## 📋 System Requirements

### Mandatory
- ☕ **JDK 17 or higher** - [Download](https://adoptium.net/)
- 🔧 **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- 💻 **IDE** - IntelliJ IDEA (recommended), Eclipse, or VS Code

### Verification
```bash
# Check Java version
java -version
# Should show: openjdk version "17" or higher

# Check Maven version
mvn -version
# Should show: Apache Maven 3.8 or higher
```

### Optional (for later modules)
- 🐳 **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop)
- 📮 **Postman** - [Download](https://www.postman.com/downloads/)

---

## 🚀 Run Your First Demo

### Step 1: Navigate to a Demo
```bash
cd 01-core-java-fundamentals/01-collections-framework/demo-arraylist-basics
```

### Step 2: Build the Project
```bash
mvn clean compile
```

### Step 3: Run the Demo
```bash
mvn exec:java
```

### Expected Output
```
============================================================
        ArrayList Basics Demonstration
============================================================

📚 Example 1: Creating ArrayList
----------------------------------------
1. Default constructor: []
2. With capacity 100: []
...
```

✅ **Success!** You've run your first demo!

---

## 📚 Learning Workflow

### 1. Read → 2. Run → 3. Modify → 4. Break → 5. Fix

#### 1. **Read** the README
Every topic has comprehensive documentation:
- Theory and concepts
- Diagrams
- Code examples
- Real-world scenarios

#### 2. **Run** the Demo
Execute the working code:
```bash
mvn clean compile
mvn exec:java
```

#### 3. **Modify** the Code
Make small changes:
- Add new elements to collections
- Create new service methods
- Change configurations

#### 4. **Break** it Intentionally
Learn by experimenting:
- Remove a dependency
- Comment out @Autowired
- Use wrong data types

#### 5. **Fix** What You Broke
Understand errors:
- Read stack traces
- Use debugger
- Refer back to documentation

---

## 🗺️ Recommended Learning Path

### Week 1-2: Foundations
- [ ] Module 01: Core Java Fundamentals (3-4 days)
- [ ] Module 02: Spring Core (4-5 days)

### Week 3-4: Spring Boot
- [ ] Module 03: Spring Boot Fundamentals (5-7 days)
- [ ] Build 2-3 practice REST APIs

### Week 5-8: Microservices
- [ ] Module 04: Microservices Architecture (7-10 days)
- [ ] Module 05: Messaging & Events (5-7 days)
- [ ] Module 06: Security (5-7 days)

### Week 9-10: Production Features
- [ ] Module 07: Observability (4-5 days)
- [ ] Module 08: Testing (3-4 days)
- [ ] Module 09: Containerization & Deployment (7-10 days)

### Week 11-14: Advanced & Capstone
- [ ] Module 10: Database Patterns (5-7 days)
- [ ] Module 11: Advanced Topics (5-7 days)
- [ ] Module 12: Capstone Project (14-21 days)

**Total Time**: 10-14 weeks with consistent daily practice (2-3 hours/day)

---

## 💡 Pro Tips

### Tip 1: Use IntelliJ IDEA Community Edition (Free)
Best IDE for Spring development:
- Auto-completion for Spring annotations
- Built-in Maven support
- Excellent debugging
- Spring Boot run configurations

### Tip 2: Create a Learning Journal
Document as you learn:
```markdown
# Day 1: Collections Framework
- Learned: ArrayList vs LinkedList performance
- Struggled with: HashMap collision resolution
- Question: When to use TreeSet vs HashSet?
- Tomorrow: Practice Stream API
```

### Tip 3: Build Mini-Projects
After each module, build something:
- Module 01: Todo list with collections
- Module 02: DI-based calculator
- Module 03: Simple blog REST API
- Module 04: Multi-service e-commerce

### Tip 4: Join Spring Community
- [Spring Community Forum](https://community.spring.io/)
- [Stack Overflow - Spring Boot](https://stackoverflow.com/questions/tagged/spring-boot)
- [r/springframework](https://reddit.com/r/springframework)

### Tip 5: Use Debug Mode
Learn by stepping through code:
```java
// Set breakpoint here
UserService service = context.getBean(UserService.class);
// Step through to see dependency injection happen!
```

---

## 🛠️ IDE Setup

### IntelliJ IDEA

#### 1. Import Project
```
File → Open → Select pom.xml → Open as Project
```

#### 2. Configure JDK
```
File → Project Structure → Project → SDK: Select JDK 17+
```

#### 3. Enable Annotation Processing
```
Settings → Build → Compiler → Annotation Processors → Enable
```

#### 4. Install Plugins (Optional)
- Spring Boot Assistant
- Lombok Plugin
- Rainbow Brackets

### VS Code

#### 1. Install Extensions
- Extension Pack for Java
- Spring Boot Extension Pack
- Maven for Java

#### 2. Open Folder
```
File → Open Folder → Select masterclass root
```

#### 3. Run Demo
```
Right-click main class → Run Java
```

### Eclipse

#### 1. Import Maven Project
```
File → Import → Maven → Existing Maven Projects
```

#### 2. Install Spring Tools
```
Help → Eclipse Marketplace → Search "Spring Tools"
```

---

## 📝 Running Different Types of Projects

### Core Java Projects (Module 01)
```bash
cd demo-directory
mvn clean compile
mvn exec:java
```

### Spring Core Projects (Module 02)
```bash
cd demo-directory
mvn clean compile
mvn exec:java
```

### Spring Boot Projects (Module 03+)
```bash
cd demo-directory
mvn spring-boot:run

# Or with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker-based Projects (Module 09+)
```bash
# Build Docker image
docker build -t my-app .

# Run container
docker run -p 8080:8080 my-app
```

---

## 🐛 Troubleshooting

### Problem: "mvn command not found"
**Solution**: Maven not in PATH
```bash
# Windows
set PATH=%PATH%;C:\path\to\maven\bin

# Mac/Linux
export PATH=$PATH:/path/to/maven/bin
```

### Problem: "Java version mismatch"
**Solution**: Check JAVA_HOME
```bash
# Windows
echo %JAVA_HOME%

# Mac/Linux
echo $JAVA_HOME

# Should point to JDK 17+
```

### Problem: "Port 8080 already in use"
**Solution**: Change port or kill process
```bash
# Change port in application.properties
server.port=8081

# Or kill process (Windows)
netstat -ano | findstr :8080
taskkill /PID <pid> /F

# Or kill process (Mac/Linux)
lsof -i :8080
kill -9 <pid>
```

### Problem: Dependencies won't download
**Solution**: Clear Maven cache
```bash
# Delete .m2/repository folder
rm -rf ~/.m2/repository

# Re-download
mvn clean install
```

### Problem: IDE doesn't recognize Spring annotations
**Solution**: Enable annotation processing and reimport
```
1. Enable annotation processing in IDE settings
2. Right-click pom.xml → Maven → Reimport
3. Rebuild project
```

---

## 📞 Getting Help

### Option 1: Check Documentation
Every module and demo has comprehensive READMEs:
- Read the theory section
- Check the examples
- Review common mistakes

### Option 2: Use the Glossary
[GLOSSARY.md](GLOSSARY.md) contains all technical terms explained simply.

### Option 3: Debug Step-by-Step
Use your IDE's debugger:
1. Set breakpoints
2. Run in debug mode
3. Step through code
4. Inspect variables

### Option 4: Search Issues
Check if others faced similar problems:
- Stack Overflow
- Spring Documentation
- GitHub Issues

### Option 5: Ask the Community
Post detailed questions:
- What you're trying to do
- What you expected
- What actually happened
- Code snippets
- Error messages

---

## ✅ First Day Checklist

- [ ] Java 17+ installed and verified
- [ ] Maven 3.8+ installed and verified
- [ ] IDE installed and configured
- [ ] Project downloaded/cloned
- [ ] Ran first demo successfully
- [ ] Read main README.md
- [ ] Bookmarked GLOSSARY.md
- [ ] Joined Spring community forum
- [ ] Created learning journal
- [ ] Set daily learning goals

---

## 🎯 Daily Practice Routine

### Morning (30 minutes)
- Read one topic's README
- Review diagrams
- Take notes

### Afternoon (1 hour)
- Run the demo
- Experiment with code
- Try exercises

### Evening (30 minutes)
- Build mini-project
- Review what you learned
- Plan tomorrow's topic

**Consistency beats intensity!** 2 hours daily for 12 weeks > 8 hours once a week.

---

## 🚀 You're Ready!

Now that you're set up, choose your starting module and begin learning!

**Remember**:
- Learn by doing, not just reading
- Break things and fix them
- Ask questions
- Build projects
- Stay consistent

---

**Start Your Journey**: [Module 01 - Core Java Fundamentals →](01-core-java-fundamentals/)

_Every expert was once a beginner. Your journey starts now! 💪_
