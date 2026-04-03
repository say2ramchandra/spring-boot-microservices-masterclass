# Cucumber BDD Demo

> **Behavior-Driven Development with Cucumber and Spring Boot**

## 📦 Project Structure

```
demo-cucumber-bdd/
├── pom.xml
├── src/test/
│   ├── java/com/masterclass/automation/
│   │   ├── CucumberTestRunner.java       # JUnit Platform Suite runner
│   │   ├── config/
│   │   │   ├── CucumberSpringConfig.java # Spring-Cucumber integration
│   │   │   └── TestConfig.java           # Spring configuration
│   │   ├── context/
│   │   │   └── TestContext.java          # Shared test state
│   │   ├── hooks/
│   │   │   └── CucumberHooks.java        # @Before/@After hooks
│   │   ├── pages/
│   │   │   ├── BasePage.java             # Common page functionality
│   │   │   ├── LoginPage.java            # Login page object
│   │   │   └── DashboardPage.java        # Dashboard page object
│   │   └── steps/
│   │       ├── LoginSteps.java           # Login feature steps
│   │       └── ApiSteps.java             # API feature steps
│   └── resources/
│       ├── cucumber.properties           # Cucumber configuration
│       └── features/
│           ├── login.feature             # Login scenarios
│           └── product_api.feature       # API scenarios
```

## 🚀 Running Tests

### Run all tests
```bash
mvn test
```

### Run by tag
```bash
# Smoke tests only
mvn test -Dcucumber.filter.tags="@smoke"

# Using Maven profile
mvn test -Psmoke

# Multiple tags (AND)
mvn test -Dcucumber.filter.tags="@smoke and @positive"

# Multiple tags (OR)
mvn test -Dcucumber.filter.tags="@login or @api"

# Exclude tags
mvn test -Dcucumber.filter.tags="@regression and not @skip"
```

### Run specific feature
```bash
mvn test -Dcucumber.features="src/test/resources/features/login.feature"
```

### Run with headless browser
```bash
mvn test -Dheadless=true
```

### Run API tests only
```bash
mvn test -Papi-only
```

## 📝 Gherkin Syntax

### Basic Scenario
```gherkin
Scenario: Login with valid credentials
  Given I am on the login page
  When I enter username "admin"
  And I enter password "password123"
  And I click the login button
  Then I should be redirected to the dashboard
```

### Scenario Outline (Data-Driven)
```gherkin
Scenario Outline: Login with multiple users
  When I enter username "<username>"
  And I enter password "<password>"
  Then I should see message "<message>"

  Examples:
    | username | password | message         |
    | admin    | admin123 | Welcome, Admin! |
    | user     | user123  | Welcome, User!  |
```

### Data Tables
```gherkin
When I create products with the following details:
  | name   | price | category    |
  | Phone  | 599   | Electronics |
  | Tablet | 399   | Electronics |
```

### Doc Strings (JSON)
```gherkin
When I send a POST request to "/api/products" with body:
  """
  {
    "name": "New Product",
    "price": 99.99
  }
  """
```

## 🏷️ Tags

| Tag | Description |
|-----|-------------|
| `@smoke` | Quick sanity tests |
| `@regression` | Full regression suite |
| `@positive` | Happy path scenarios |
| `@negative` | Error handling scenarios |
| `@api` | API-only tests (no browser) |
| `@ui` | UI tests (requires browser) |
| `@login` | Login feature tests |
| `@data-driven` | Parameterized tests |
| `@skip` | Skip these tests |

## 📊 Reports

After running tests, reports are generated in:
- `target/cucumber-reports/cucumber.html` - HTML report
- `target/cucumber-reports/cucumber.json` - JSON report

## 🔑 Key Features Demonstrated

### 1. Spring Integration
Step definitions can autowire Spring beans:
```java
@Autowired
private TestContext testContext;
```

### 2. Hooks
- `@BeforeAll` - One-time setup
- `@Before` - Before each scenario
- `@Before("@ui")` - Conditional hooks by tag
- `@After` - After each scenario (screenshot on failure)
- `@AfterStep` - After each step

### 3. Test Context
Shared state between steps:
```java
testContext.setDriver(driver);
testContext.setLastResponse(response);
```

### 4. API + UI Testing
Same framework supports both:
- `@api` scenarios use REST Assured
- `@ui` scenarios use Selenium

## 📚 Technologies

- Cucumber 7.14.1
- Spring Boot 3.2.0
- Selenium 4.15.0
- REST Assured 5.3.2
- JUnit Platform Suite 1.10.1
