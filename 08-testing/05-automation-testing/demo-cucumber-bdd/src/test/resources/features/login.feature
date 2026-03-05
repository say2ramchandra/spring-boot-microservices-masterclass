# features/login.feature

@login @smoke
Feature: User Login
  As a registered user
  I want to login to the application
  So that I can access my account dashboard

  Background:
    Given I am on the login page

  @positive @smoke
  Scenario: Successful login with valid credentials
    When I enter username "admin"
    And I enter password "password123"
    And I click the login button
    Then I should be redirected to the dashboard
    And I should see welcome message "Welcome, Admin!"

  @positive
  Scenario: Login with remember me option
    When I enter username "admin"
    And I enter password "password123"
    And I check the remember me checkbox
    And I click the login button
    Then I should be redirected to the dashboard

  @negative
  Scenario: Login fails with invalid password
    When I enter username "admin"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should see error message "Invalid username or password"
    And I should remain on the login page

  @negative
  Scenario: Login fails with invalid username
    When I enter username "invaliduser"
    And I enter password "password123"
    And I click the login button
    Then I should see error message "Invalid username or password"
    And I should remain on the login page

  @negative
  Scenario: Login fails with empty credentials
    When I leave username empty
    And I leave password empty
    And I click the login button
    Then I should see error message "Username is required"
    And I should remain on the login page

  @data-driven @regression
  Scenario Outline: Login with multiple user roles
    When I enter username "<username>"
    And I enter password "<password>"
    And I click the login button
    Then I should see welcome message "<welcome_message>"
    And I should have role "<role>"

    Examples:
      | username | password  | welcome_message    | role   |
      | admin    | admin123  | Welcome, Admin!    | ADMIN  |
      | editor   | editor123 | Welcome, Editor!   | EDITOR |
      | viewer   | viewer123 | Welcome, Viewer!   | VIEWER |

  @security
  Scenario: Account locks after multiple failed attempts
    When I enter username "admin"
    And I enter password "wrongpassword"
    And I click the login button
    And I enter password "wrongagain"
    And I click the login button
    And I enter password "stilwrong"
    And I click the login button
    Then I should see error message "Account locked"
    And the login button should be disabled
