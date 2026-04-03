# Selenium Basics Demo

> **Selenium WebDriver fundamentals with Page Object Model**

## 📦 Project Structure

```
demo-selenium-basics/
├── pom.xml
├── src/
│   ├── main/java/com/masterclass/automation/
│   │   ├── driver/
│   │   │   └── WebDriverFactory.java      # Thread-safe driver factory
│   │   └── pages/
│   │       ├── BasePage.java              # Common page functionality
│   │       ├── LoginPage.java             # Login page object
│   │       ├── DashboardPage.java         # Dashboard page object
│   │       └── SearchPage.java            # Search page object
│   └── test/java/com/masterclass/automation/tests/
│       ├── BaseTest.java                  # Base test with setup/teardown
│       └── LoginTest.java                 # Login test examples
```

## 🚀 Running Tests

### Run all tests
```bash
mvn test
```

### Run with specific browser
```bash
# Chrome (default)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge
```

### Run in headless mode (for CI/CD)
```bash
mvn test -Dheadless=true
```

### Run with custom base URL
```bash
mvn test -DbaseUrl=https://your-app.com
```

### Run specific test class
```bash
mvn test -Dtest=LoginTest
```

### Using profiles
```bash
# Headless Chrome
mvn test -Pheadless,chrome

# Firefox
mvn test -Pfirefox
```

## 🔑 Key Concepts

### 1. WebDriverFactory
Thread-safe WebDriver creation for parallel test execution:
```java
WebDriver driver = WebDriverFactory.getDriver();
// ... run tests
WebDriverFactory.quitDriver();
```

### 2. Page Object Model (POM)
Each page is represented by a class with:
- `@FindBy` elements
- Action methods (click, type, etc.)
- Verification methods (isDisplayed, getText, etc.)

```java
LoginPage loginPage = new LoginPage(driver);
DashboardPage dashboard = loginPage.loginAs("admin", "password");
```

### 3. Fluent Interface
Methods return `this` for chaining:
```java
loginPage
    .enterUsername("admin")
    .enterPassword("password")
    .checkRememberMe()
    .clickLogin();
```

### 4. Base Page Pattern
Common functionality in `BasePage`:
- Wait methods (waitForVisible, waitForClickable)
- Action methods (click, type, selectByVisibleText)
- Verification methods (isDisplayed, isEnabled)

## 📝 Example Test

```java
@Test
@DisplayName("Should login successfully with valid credentials")
void testValidLogin() {
    // Arrange
    navigateTo("/login");
    LoginPage loginPage = new LoginPage(driver);

    // Act
    DashboardPage dashboard = loginPage.loginAs("admin", "password123");

    // Assert
    assertThat(dashboard.isWelcomeMessageDisplayed()).isTrue();
    assertThat(dashboard.getWelcomeMessage()).containsIgnoringCase("admin");
}
```

## 📊 Screenshots

Screenshots are automatically saved on test failure to `target/screenshots/`.

## 🎯 Best Practices Demonstrated

1. **Page Object Model** - Separation of concerns
2. **Thread-safe drivers** - For parallel execution
3. **Explicit waits** - No Thread.sleep()
4. **Fluent assertions** - Using AssertJ
5. **Configurable execution** - Browser, headless, base URL
6. **Base test class** - Common setup/teardown

## 📚 Technologies

- Selenium WebDriver 4.15.0
- WebDriverManager 5.6.2 (auto driver management)
- JUnit 5.10.1
- AssertJ 3.24.2
