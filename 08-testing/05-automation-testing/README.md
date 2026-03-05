# Automation Testing

> **Master Selenium, Cucumber BDD, and TestNG for comprehensive test automation**

---

## 📚 Overview

Automation testing is essential for ensuring software quality at scale. This section covers three key technologies:

| Tool | Purpose | Best For |
|------|---------|----------|
| **Selenium WebDriver** | Browser automation | UI/E2E testing |
| **Cucumber** | BDD framework | Acceptance testing |
| **TestNG** | Test framework | Advanced test organization |

---

## 🎯 Learning Objectives

After completing this section, you will:
- ✅ Automate web browsers using Selenium WebDriver
- ✅ Write BDD tests with Cucumber and Gherkin
- ✅ Use TestNG for advanced test configurations
- ✅ Implement Page Object Model pattern
- ✅ Generate test reports with Allure/ExtentReports
- ✅ Run parallel tests for faster execution

---

## 📦 Section Structure

```
05-automation-testing/
├── README.md
├── demo-selenium-basics/       # Selenium WebDriver fundamentals
├── demo-cucumber-bdd/          # Cucumber with Spring Boot
└── demo-testng-framework/      # TestNG vs JUnit, parallel tests
```

---

# Part 1: Selenium WebDriver

## What is Selenium?

Selenium is the industry standard for browser automation. It supports:
- Multiple browsers (Chrome, Firefox, Edge, Safari)
- Multiple programming languages (Java, Python, C#, JavaScript)
- Cross-platform testing (Windows, macOS, Linux)

## Selenium Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Test Script (Java)                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    WebDriver API                             │
│  (ChromeDriver, GeckoDriver, EdgeDriver, SafariDriver)      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Browser Driver                           │
│           (chromedriver.exe, geckodriver.exe)               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Browser                               │
│              (Chrome, Firefox, Edge, Safari)                 │
└─────────────────────────────────────────────────────────────┘
```

## WebDriver Setup

### Maven Dependencies

```xml
<dependencies>
    <!-- Selenium WebDriver -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
    </dependency>
    
    <!-- WebDriverManager - Auto driver management -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.6.2</version>
    </dependency>
    
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Basic WebDriver Usage

```java
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverSetup {
    
    public static WebDriver createChromeDriver() {
        // Auto-download and setup ChromeDriver
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        // options.addArguments("--headless");  // For CI/CD
        
        return new ChromeDriver(options);
    }
}
```

## Locating Elements

Selenium provides multiple strategies to find elements:

### By ID (Fastest)
```java
WebElement element = driver.findElement(By.id("username"));
```

### By Name
```java
WebElement element = driver.findElement(By.name("email"));
```

### By Class Name
```java
WebElement element = driver.findElement(By.className("btn-primary"));
```

### By CSS Selector (Recommended)
```java
// By ID
WebElement byId = driver.findElement(By.cssSelector("#username"));

// By class
WebElement byClass = driver.findElement(By.cssSelector(".btn-primary"));

// By attribute
WebElement byAttr = driver.findElement(By.cssSelector("[data-testid='login-btn']"));

// Combination
WebElement combo = driver.findElement(By.cssSelector("form#login input[type='email']"));
```

### By XPath (Most Flexible)
```java
// Absolute (avoid - fragile)
WebElement abs = driver.findElement(By.xpath("/html/body/div/form/input[1]"));

// Relative (preferred)
WebElement rel = driver.findElement(By.xpath("//input[@id='username']"));

// Contains text
WebElement text = driver.findElement(By.xpath("//button[contains(text(), 'Submit')]"));

// Parent/sibling navigation
WebElement parent = driver.findElement(By.xpath("//input[@id='email']/parent::div"));
```

### By Link Text
```java
WebElement link = driver.findElement(By.linkText("Click here"));
WebElement partialLink = driver.findElement(By.partialLinkText("Click"));
```

## Common WebDriver Operations

### Navigation
```java
// Open URL
driver.get("https://example.com");
driver.navigate().to("https://example.com");

// Browser navigation
driver.navigate().back();
driver.navigate().forward();
driver.navigate().refresh();

// Get current info
String title = driver.getTitle();
String url = driver.getCurrentUrl();
String pageSource = driver.getPageSource();
```

### Element Interactions
```java
WebElement element = driver.findElement(By.id("username"));

// Input
element.sendKeys("john_doe");
element.clear();
element.sendKeys(Keys.ENTER);

// Click
element.click();

// Read
String text = element.getText();
String value = element.getAttribute("value");
String cssValue = element.getCssValue("color");

// State
boolean displayed = element.isDisplayed();
boolean enabled = element.isEnabled();
boolean selected = element.isSelected();
```

### Working with Dropdowns
```java
import org.openqa.selenium.support.ui.Select;

WebElement dropdown = driver.findElement(By.id("country"));
Select select = new Select(dropdown);

// Select by visible text
select.selectByVisibleText("United States");

// Select by value attribute
select.selectByValue("US");

// Select by index (0-based)
select.selectByIndex(2);

// Get selected option
WebElement selected = select.getFirstSelectedOption();
List<WebElement> allSelected = select.getAllSelectedOptions();

// Get all options
List<WebElement> options = select.getOptions();
```

### Handling Alerts
```java
// Switch to alert
Alert alert = driver.switchTo().alert();

// Get alert text
String alertText = alert.getText();

// Accept (OK)
alert.accept();

// Dismiss (Cancel)
alert.dismiss();

// Enter text in prompt
alert.sendKeys("Input text");
```

### Handling Windows/Tabs
```java
// Get current window handle
String mainWindow = driver.getWindowHandle();

// Get all window handles
Set<String> allWindows = driver.getWindowHandles();

// Switch to new window
for (String window : allWindows) {
    if (!window.equals(mainWindow)) {
        driver.switchTo().window(window);
        break;
    }
}

// Switch back to main window
driver.switchTo().window(mainWindow);

// Close current window
driver.close();

// Quit browser (closes all windows)
driver.quit();
```

### Handling Frames
```java
// Switch by index
driver.switchTo().frame(0);

// Switch by name or ID
driver.switchTo().frame("frameName");

// Switch by WebElement
WebElement frameElement = driver.findElement(By.id("myFrame"));
driver.switchTo().frame(frameElement);

// Switch back to main content
driver.switchTo().defaultContent();

// Switch to parent frame
driver.switchTo().parentFrame();
```

## Waits

### Implicit Wait (Global)
```java
// Applied to all findElement calls
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
```

### Explicit Wait (Specific Conditions)
```java
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for element to be visible
WebElement element = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("result"))
);

// Wait for element to be clickable
WebElement button = wait.until(
    ExpectedConditions.elementToBeClickable(By.id("submit"))
);

// Wait for text to be present
wait.until(
    ExpectedConditions.textToBePresentInElementLocated(By.id("status"), "Complete")
);

// Wait for element to disappear
wait.until(
    ExpectedConditions.invisibilityOfElementLocated(By.id("loading"))
);

// Wait for URL to contain
wait.until(ExpectedConditions.urlContains("/dashboard"));

// Custom condition
wait.until(driver -> driver.findElements(By.className("item")).size() > 5);
```

### Fluent Wait (Custom Polling)
```java
import org.openqa.selenium.support.ui.FluentWait;

FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
    .withTimeout(Duration.ofSeconds(30))
    .pollingEvery(Duration.ofMillis(500))
    .ignoring(NoSuchElementException.class)
    .ignoring(StaleElementReferenceException.class);

WebElement element = fluentWait.until(driver -> 
    driver.findElement(By.id("dynamicElement"))
);
```

## Page Object Model (POM)

The Page Object Model is a design pattern that creates an object repository for UI elements.

### Benefits
- **Maintainability**: Changes in one place
- **Reusability**: Share page objects across tests
- **Readability**: Test code reads like business logic

### Page Class Example

```java
package com.masterclass.automation.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    // Page elements using @FindBy
    @FindBy(id = "username")
    private WebElement usernameInput;
    
    @FindBy(id = "password")
    private WebElement passwordInput;
    
    @FindBy(css = "button[type='submit']")
    private WebElement loginButton;
    
    @FindBy(className = "error-message")
    private WebElement errorMessage;
    
    @FindBy(linkText = "Forgot Password?")
    private WebElement forgotPasswordLink;
    
    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    // Page actions
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }
    
    public DashboardPage clickLogin() {
        loginButton.click();
        return new DashboardPage(driver);
    }
    
    // Fluent interface for chaining
    public LoginPage withUsername(String username) {
        enterUsername(username);
        return this;
    }
    
    public LoginPage withPassword(String password) {
        enterPassword(password);
        return this;
    }
    
    public DashboardPage login() {
        return clickLogin();
    }
    
    // High-level action
    public DashboardPage loginAs(String username, String password) {
        return withUsername(username)
                .withPassword(password)
                .login();
    }
    
    // Verification methods
    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }
    
    public boolean isErrorDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Test Using Page Objects

```java
package com.masterclass.automation.tests;

import com.masterclass.automation.pages.LoginPage;
import com.masterclass.automation.pages.DashboardPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {
    
    private WebDriver driver;
    private LoginPage loginPage;
    
    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }
    
    @BeforeEach
    void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://example.com/login");
        loginPage = new LoginPage(driver);
    }
    
    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    @Test
    @DisplayName("Valid user can login successfully")
    void testValidLogin() {
        DashboardPage dashboard = loginPage.loginAs("admin", "password123");
        
        assertTrue(dashboard.isWelcomeMessageDisplayed());
        assertEquals("Welcome, Admin!", dashboard.getWelcomeMessage());
    }
    
    @Test
    @DisplayName("Invalid credentials show error message")
    void testInvalidLogin() {
        loginPage.withUsername("invalid")
                 .withPassword("wrong")
                 .clickLogin();
        
        assertTrue(loginPage.isErrorDisplayed());
        assertEquals("Invalid username or password", loginPage.getErrorMessage());
    }
}
```

## Screenshots and Reporting

### Taking Screenshots

```java
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;
import java.nio.file.Files;

public class ScreenshotUtil {
    
    public static void takeScreenshot(WebDriver driver, String fileName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File source = ts.getScreenshotAs(OutputType.FILE);
            File destination = new File("screenshots/" + fileName + ".png");
            Files.copy(source.toPath(), destination.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Screenshot on failure (JUnit 5 extension)
    public static byte[] getScreenshotAsBytes(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
```

### JUnit 5 Extension for Screenshots

```java
import org.junit.jupiter.api.extension.*;

public class ScreenshotOnFailureExtension implements TestWatcher {
    
    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        // Get WebDriver from test instance
        Object testInstance = context.getRequiredTestInstance();
        // Take screenshot using reflection to access driver field
        // Save with test name as filename
    }
}
```

---

# Part 2: Cucumber BDD

## What is Cucumber?

Cucumber is a BDD (Behavior-Driven Development) framework that allows you to write tests in plain English using Gherkin syntax.

## Benefits of BDD

- **Collaboration**: Business stakeholders can understand tests
- **Living documentation**: Tests document system behavior
- **Requirement clarity**: Forces clear acceptance criteria
- **Traceability**: Link tests to user stories

## Gherkin Syntax

### Feature File Structure

```gherkin
# features/login.feature

@login @smoke
Feature: User Login
  As a registered user
  I want to login to the application
  So that I can access my account

  Background:
    Given I am on the login page

  @positive
  Scenario: Successful login with valid credentials
    When I enter username "admin"
    And I enter password "password123"
    And I click the login button
    Then I should be redirected to the dashboard
    And I should see welcome message "Welcome, Admin!"

  @negative
  Scenario: Failed login with invalid password
    When I enter username "admin"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should see error message "Invalid credentials"
    And I should remain on the login page
```

### Scenario Outline (Data-Driven)

```gherkin
@data-driven
Scenario Outline: Login with multiple users
  When I enter username "<username>"
  And I enter password "<password>"
  And I click the login button
  Then I should see message "<message>"

  Examples:
    | username | password    | message              |
    | admin    | admin123    | Welcome, Admin!      |
    | user     | user123     | Welcome, User!       |
    | guest    | guest123    | Welcome, Guest!      |
    | invalid  | wrong       | Invalid credentials  |
```

### Data Tables

```gherkin
Scenario: Create multiple products
  When I create the following products:
    | name        | price  | category    |
    | Laptop      | 999.99 | Electronics |
    | Keyboard    | 79.99  | Electronics |
    | Mouse       | 29.99  | Electronics |
  Then all products should be saved successfully
```

## Maven Dependencies

```xml
<dependencies>
    <!-- Cucumber -->
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-java</artifactId>
        <version>7.14.1</version>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-junit-platform-engine</artifactId>
        <version>7.14.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Cucumber Spring Integration -->
    <dependency>
        <groupId>io.cucumber</groupId>
        <artifactId>cucumber-spring</artifactId>
        <version>7.14.1</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit Platform -->
    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-suite</artifactId>
        <version>1.10.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Step Definitions

### Basic Step Definitions

```java
package com.masterclass.automation.steps;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {
    
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private String currentPage;
    
    @Given("I am on the login page")
    public void iAmOnTheLoginPage() {
        // Navigate to login page
        loginPage = new LoginPage(WebDriverHolder.getDriver());
    }
    
    @When("I enter username {string}")
    public void iEnterUsername(String username) {
        loginPage.enterUsername(username);
    }
    
    @When("I enter password {string}")
    public void iEnterPassword(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("I click the login button")
    public void iClickTheLoginButton() {
        dashboardPage = loginPage.clickLogin();
    }
    
    @Then("I should be redirected to the dashboard")
    public void iShouldBeRedirectedToTheDashboard() {
        assertTrue(WebDriverHolder.getDriver()
            .getCurrentUrl().contains("/dashboard"));
    }
    
    @Then("I should see welcome message {string}")
    public void iShouldSeeWelcomeMessage(String expectedMessage) {
        assertEquals(expectedMessage, dashboardPage.getWelcomeMessage());
    }
    
    @Then("I should see error message {string}")
    public void iShouldSeeErrorMessage(String expectedError) {
        assertEquals(expectedError, loginPage.getErrorMessage());
    }
    
    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        assertTrue(WebDriverHolder.getDriver()
            .getCurrentUrl().contains("/login"));
    }
}
```

### Data Table Step Definitions

```java
import io.cucumber.datatable.DataTable;
import java.util.List;
import java.util.Map;

public class ProductSteps {
    
    @When("I create the following products:")
    public void iCreateTheFollowingProducts(DataTable dataTable) {
        // Convert to list of maps
        List<Map<String, String>> products = dataTable.asMaps();
        
        for (Map<String, String> product : products) {
            String name = product.get("name");
            String price = product.get("price");
            String category = product.get("category");
            
            // Create product
            productService.create(name, Double.parseDouble(price), category);
        }
    }
    
    // Alternative: Convert to list of lists
    @When("I create products from list:")
    public void iCreateProductsFromList(DataTable dataTable) {
        List<List<String>> rows = dataTable.asLists();
        
        // Skip header row
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String name = row.get(0);
            String price = row.get(1);
            // ...
        }
    }
}
```

## Hooks

```java
package com.masterclass.automation.hooks;

import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class CucumberHooks {
    
    private static WebDriver driver;
    
    @BeforeAll
    public static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }
    
    @Before
    public void before(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName());
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        WebDriverHolder.setDriver(driver);
    }
    
    @Before("@login")
    public void beforeLoginScenarios() {
        driver.get("https://example.com/login");
    }
    
    @After
    public void after(Scenario scenario) {
        if (scenario.isFailed()) {
            // Take screenshot on failure
            byte[] screenshot = ((TakesScreenshot) driver)
                .getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "failure-screenshot");
        }
        
        if (driver != null) {
            driver.quit();
        }
    }
    
    @AfterStep
    public void afterStep(Scenario scenario) {
        // Take screenshot after each step (optional)
    }
}
```

## Cucumber with Spring Boot

### Spring Configuration

```java
package com.masterclass.automation.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfig {
    // This class enables Spring dependency injection in step definitions
}
```

### Injecting Spring Beans

```java
package com.masterclass.automation.steps;

import org.springframework.beans.factory.annotation.Autowired;

public class ApiSteps {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @When("I call the product API")
    public void iCallTheProductApi() {
        ResponseEntity<Product[]> response = restTemplate
            .getForEntity("/api/products", Product[].class);
        // ...
    }
}
```

## Running Cucumber Tests

### Test Runner Class

```java
package com.masterclass.automation;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, 
    value = "pretty, html:target/cucumber-reports/cucumber.html, json:target/cucumber-reports/cucumber.json")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, 
    value = "com.masterclass.automation.steps,com.masterclass.automation.hooks")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, 
    value = "@smoke and not @skip")
public class CucumberTestRunner {
}
```

### cucumber.properties

```properties
# src/test/resources/cucumber.properties

cucumber.publish.quiet=true
cucumber.plugin=pretty,html:target/cucumber-reports.html
cucumber.snippet-type=camelcase
```

---

# Part 3: TestNG Framework

## What is TestNG?

TestNG is a testing framework inspired by JUnit but with more powerful features:

| Feature | JUnit 5 | TestNG |
|---------|---------|--------|
| Parallel execution | Via config | Built-in annotations |
| Data providers | @ParameterizedTest | @DataProvider |
| Groups | @Tag | @Test(groups) |
| Dependencies | @Order | dependsOnMethods |
| Listeners | Extensions | ITestListener |
| Reports | Third-party | Built-in HTML |

## Maven Dependencies

```xml
<dependencies>
    <!-- TestNG -->
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.8.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- Selenium -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.15.0</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.2</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>testng.xml</suiteXmlFile>
                </suiteXmlFiles>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## TestNG Annotations

### Lifecycle Annotations

```java
import org.testng.annotations.*;

public class TestNGLifecycle {
    
    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Before Suite - runs once per suite");
    }
    
    @AfterSuite
    public void afterSuite() {
        System.out.println("After Suite");
    }
    
    @BeforeTest
    public void beforeTest() {
        System.out.println("Before Test - runs once per <test> in testng.xml");
    }
    
    @AfterTest
    public void afterTest() {
        System.out.println("After Test");
    }
    
    @BeforeClass
    public void beforeClass() {
        System.out.println("Before Class - runs once per test class");
    }
    
    @AfterClass
    public void afterClass() {
        System.out.println("After Class");
    }
    
    @BeforeMethod
    public void beforeMethod() {
        System.out.println("Before Method - runs before each @Test");
    }
    
    @AfterMethod
    public void afterMethod() {
        System.out.println("After Method");
    }
    
    @Test
    public void testMethod() {
        System.out.println("Test Method");
    }
}
```

### @Test Annotation Attributes

```java
public class TestAttributes {
    
    @Test(
        description = "Verify login functionality",
        priority = 1,
        enabled = true,
        timeOut = 5000,
        groups = {"smoke", "regression"},
        dependsOnMethods = {"testSetup"},
        invocationCount = 3,
        threadPoolSize = 3,
        expectedExceptions = {NullPointerException.class}
    )
    public void testLogin() {
        // Test code
    }
    
    @Test(priority = 0)
    public void testSetup() {
        // Runs first due to priority
    }
}
```

## Data Providers

### Basic Data Provider

```java
public class DataProviderExample {
    
    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][] {
            {"admin", "admin123", true},
            {"user", "user123", true},
            {"invalid", "wrong", false}
        };
    }
    
    @Test(dataProvider = "loginData")
    public void testLogin(String username, String password, boolean shouldPass) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(username, password);
        
        if (shouldPass) {
            assertTrue(isLoggedIn());
        } else {
            assertTrue(loginPage.isErrorDisplayed());
        }
    }
}
```

### Data Provider from External Source

```java
public class ExternalDataProvider {
    
    @DataProvider(name = "excelData")
    public Object[][] getExcelData() {
        // Read from Excel file
        List<Object[]> data = ExcelReader.read("testdata.xlsx", "LoginData");
        return data.toArray(new Object[0][]);
    }
    
    @DataProvider(name = "csvData")
    public Iterator<Object[]> getCsvData() {
        // Stream from CSV file
        return CsvReader.stream("testdata.csv")
            .map(row -> new Object[]{row[0], row[1], Boolean.parseBoolean(row[2])})
            .iterator();
    }
    
    @DataProvider(name = "jsonData")
    public Object[][] getJsonData() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        TestData[] testData = mapper.readValue(
            new File("testdata.json"), TestData[].class);
        
        return Arrays.stream(testData)
            .map(td -> new Object[]{td.username, td.password, td.expected})
            .toArray(Object[][]::new);
    }
}
```

## Test Groups

```java
public class GroupsExample {
    
    @Test(groups = {"smoke"})
    public void smokeTest1() {
        // Quick sanity test
    }
    
    @Test(groups = {"smoke", "regression"})
    public void smokeTest2() {
        // Part of both groups
    }
    
    @Test(groups = {"regression"})
    public void regressionTest() {
        // Full regression test
    }
    
    @Test(groups = {"integration"}, dependsOnGroups = {"smoke"})
    public void integrationTest() {
        // Runs after all smoke tests
    }
}
```

## Parallel Execution

### testng.xml Configuration

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">

<suite name="Automation Suite" parallel="tests" thread-count="3">
    
    <listeners>
        <listener class-name="com.masterclass.automation.listeners.TestListener"/>
    </listeners>
    
    <test name="Smoke Tests">
        <groups>
            <run>
                <include name="smoke"/>
            </run>
        </groups>
        <classes>
            <class name="com.masterclass.automation.tests.LoginTest"/>
            <class name="com.masterclass.automation.tests.HomeTest"/>
        </classes>
    </test>
    
    <test name="Regression Tests" parallel="methods" thread-count="5">
        <groups>
            <run>
                <include name="regression"/>
                <exclude name="skip"/>
            </run>
        </groups>
        <packages>
            <package name="com.masterclass.automation.tests.*"/>
        </packages>
    </test>
    
</suite>
```

### Parallel Modes

| Mode | Description |
|------|-------------|
| `parallel="tests"` | Each `<test>` tag runs in parallel |
| `parallel="classes"` | Each test class runs in parallel |
| `parallel="methods"` | Each test method runs in parallel |
| `parallel="instances"` | Each instance runs in parallel |

### Thread-Safe WebDriver

```java
public class WebDriverFactory {
    
    private static ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();
    
    public static WebDriver getDriver() {
        if (driverThread.get() == null) {
            WebDriverManager.chromedriver().setup();
            driverThread.set(new ChromeDriver());
        }
        return driverThread.get();
    }
    
    public static void quitDriver() {
        if (driverThread.get() != null) {
            driverThread.get().quit();
            driverThread.remove();
        }
    }
}
```

## TestNG Listeners

### ITestListener

```java
import org.testng.*;

public class TestListener implements ITestListener {
    
    @Override
    public void onStart(ITestContext context) {
        System.out.println("Suite started: " + context.getName());
    }
    
    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Suite finished: " + context.getName());
        System.out.println("Passed: " + context.getPassedTests().size());
        System.out.println("Failed: " + context.getFailedTests().size());
        System.out.println("Skipped: " + context.getSkippedTests().size());
    }
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test started: " + result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());
        
        // Take screenshot
        WebDriver driver = WebDriverFactory.getDriver();
        if (driver != null) {
            ScreenshotUtil.takeScreenshot(driver, result.getName());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test skipped: " + result.getName());
    }
}
```

### Retry Failed Tests

```java
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    
    private int retryCount = 0;
    private static final int MAX_RETRY = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            System.out.println("Retrying test: " + result.getName() + 
                " (Attempt " + retryCount + ")");
            return true;
        }
        return false;
    }
}

// Usage
@Test(retryAnalyzer = RetryAnalyzer.class)
public void flakyTest() {
    // This test will retry up to 2 times on failure
}
```

---

## 🎯 Best Practices Summary

### General
1. **Use Page Object Model** - Separate test logic from page interactions
2. **Use explicit waits** - Avoid Thread.sleep()
3. **Take screenshots on failure** - For debugging
4. **Use data-driven testing** - Maximize test coverage
5. **Run tests in parallel** - Faster feedback

### Selenium
- Use CSS selectors over XPath when possible
- Use `data-testid` attributes for stable locators
- Implement proper wait strategies
- Handle dynamic elements properly

### Cucumber
- Write scenarios from user perspective
- Keep steps reusable and atomic
- Use Background for common setup
- Tag scenarios for selective execution

### TestNG
- Use groups for test organization
- Implement retry for flaky tests
- Use listeners for reporting
- Configure parallel execution for speed

---

## 📚 Demos in This Section

| Demo | Description | Key Concepts |
|------|-------------|--------------|
| demo-selenium-basics | Selenium fundamentals | Element locators, waits, POM |
| demo-cucumber-bdd | Cucumber with Spring | Gherkin, steps, hooks |
| demo-testng-framework | TestNG features | Data providers, parallel, groups |

---

## 🔗 Related Modules

- [Module 08: Testing](../README.md) - Unit, Integration, Contract testing
- [Module 04: E2E Testing](../04-e2e-testing/README.md) - REST Assured

---

## 📖 Additional Resources

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [WebDriverManager](https://github.com/bonigarcia/webdrivermanager)
