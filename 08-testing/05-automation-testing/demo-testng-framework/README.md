# TestNG Framework Demo

> **TestNG features: parallel execution, data providers, groups, and listeners**

## 📦 Project Structure

```
demo-testng-framework/
├── pom.xml
├── testng.xml                    # Main suite configuration
├── testng-smoke.xml              # Smoke test suite
├── testng-parallel.xml           # Parallel execution suite
├── src/
│   ├── main/java/com/masterclass/automation/
│   │   └── driver/
│   │       └── DriverFactory.java         # Thread-safe driver factory
│   └── test/java/com/masterclass/automation/
│       ├── tests/
│       │   ├── BaseTest.java              # Base test class
│       │   ├── LoginTest.java             # Login tests with data providers
│       │   └── SearchTest.java            # Search tests with groups
│       └── listeners/
│           ├── TestListener.java          # ITestListener implementation
│           └── RetryAnalyzer.java         # Retry failed tests
```

## 🚀 Running Tests

### Run with default testng.xml
```bash
mvn test
```

### Run smoke tests
```bash
mvn test -Psmoke
# or
mvn test -DsuiteXmlFile=testng-smoke.xml
```

### Run in parallel
```bash
mvn test -Pparallel
```

### Run specific groups
```bash
# Using testng.xml with group configuration
mvn test -Dgroups=regression

# Using command line
mvn test -Dtest=LoginTest -Dgroups=smoke,login
```

### Run with headless browser
```bash
mvn test -Dheadless=true
```

### Run with Firefox
```bash
mvn test -Dbrowser=firefox
```

## 🏷️ Test Groups

| Group | Description |
|-------|-------------|
| `smoke` | Quick sanity tests |
| `regression` | Full regression suite |
| `login` | Login functionality |
| `search` | Search functionality |
| `negative` | Error handling tests |
| `parallel` | Tests designed for parallel execution |
| `performance` | Performance/timeout tests |
| `flaky` | Potentially unstable tests |
| `dependencies` | Tests with dependencies |

## 📊 TestNG Features Demonstrated

### 1. Data Providers
```java
@DataProvider(name = "loginData")
public Object[][] loginData() {
    return new Object[][] {
        {"admin", "admin123", "Welcome, Admin!"},
        {"user", "user123", "Welcome, User!"}
    };
}

@Test(dataProvider = "loginData")
public void testLogin(String username, String password, String expected) {
    // Test with each data row
}
```

### 2. Parallel Data Provider
```java
@DataProvider(name = "parallelData", parallel = true)
public Object[][] parallelData() { ... }

@Test(dataProvider = "parallelData", threadPoolSize = 4)
public void testParallel(String data) { ... }
```

### 3. Test Groups
```java
@Test(groups = {"smoke", "login"}, priority = 1)
public void testValidLogin() { ... }

@Test(groups = {"regression"}, dependsOnGroups = {"smoke"})
public void testAdvancedLogin() { ... }
```

### 4. Test Dependencies
```java
@Test
public void testSetup() { ... }

@Test(dependsOnMethods = {"testSetup"})
public void testAfterSetup() { ... }
```

### 5. Retry Analyzer
```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testFlakyOperation() { ... }
```

### 6. Timeout
```java
@Test(timeOut = 5000)  // 5 seconds
public void testPerformance() { ... }
```

### 7. Expected Exceptions
```java
@Test(expectedExceptions = IllegalArgumentException.class)
public void testInvalidInput() { ... }
```

### 8. Invocation Count
```java
@Test(invocationCount = 3)
public void testRepeated() { ... }

@Test(invocationCount = 10, threadPoolSize = 5)
public void testConcurrent() { ... }
```

## 📝 testng.xml Configuration

### Parallel Modes
```xml
<!-- Run each <test> in parallel -->
<suite parallel="tests" thread-count="3">

<!-- Run each class in parallel -->
<suite parallel="classes" thread-count="3">

<!-- Run each method in parallel -->
<suite parallel="methods" thread-count="5">
```

### Group Selection
```xml
<groups>
    <run>
        <include name="smoke"/>
        <include name="regression"/>
        <exclude name="flaky"/>
    </run>
</groups>
```

### Listeners
```xml
<listeners>
    <listener class-name="com.masterclass.automation.listeners.TestListener"/>
</listeners>
```

## 📈 Reports

TestNG generates reports in:
- `target/surefire-reports/` - JUnit XML reports
- `target/surefire-reports/html/` - HTML report
- `target/surefire-reports/testng-results.xml` - TestNG XML

## 🔑 TestNG vs JUnit 5 Comparison

| Feature | TestNG | JUnit 5 |
|---------|--------|---------|
| Annotation | @Test | @Test |
| Setup | @BeforeMethod | @BeforeEach |
| Teardown | @AfterMethod | @AfterEach |
| Class setup | @BeforeClass | @BeforeAll |
| Data provider | @DataProvider | @ParameterizedTest |
| Groups | @Test(groups={}) | @Tag |
| Dependencies | dependsOnMethods | @Order or extensions |
| Parallel | XML config | junit-platform.properties |
| Listeners | ITestListener | Extension |

## 📚 Technologies

- TestNG 7.8.0
- Selenium 4.15.0
- WebDriverManager 5.6.2
- AssertJ 3.24.2
- ExtentReports 5.1.1 (optional)
