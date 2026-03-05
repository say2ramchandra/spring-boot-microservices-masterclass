# features/product_api.feature

@api @products
Feature: Product API
  As an API consumer
  I want to manage products via REST API
  So that I can integrate with the product catalog

  Background:
    Given the API is available

  @smoke @api
  Scenario: Get all products
    When I send a GET request to "/api/products"
    Then the response status code should be 200
    And the response should contain a list of products

  @api
  Scenario: Get product by ID
    Given a product exists with ID 1
    When I send a GET request to "/api/products/1"
    Then the response status code should be 200
    And the response should contain product with name "Laptop"

  @api
  Scenario: Create a new product
    When I send a POST request to "/api/products" with body:
      """
      {
        "name": "New Product",
        "description": "Test product description",
        "price": 99.99,
        "category": "Electronics"
      }
      """
    Then the response status code should be 201
    And the response should contain the created product ID

  @api
  Scenario: Update an existing product
    Given a product exists with ID 1
    When I send a PUT request to "/api/products/1" with body:
      """
      {
        "name": "Updated Product",
        "price": 149.99
      }
      """
    Then the response status code should be 200
    And the product name should be "Updated Product"

  @api
  Scenario: Delete a product
    Given a product exists with ID 99
    When I send a DELETE request to "/api/products/99"
    Then the response status code should be 204
    And the product with ID 99 should not exist

  @api @negative
  Scenario: Get non-existent product returns 404
    When I send a GET request to "/api/products/99999"
    Then the response status code should be 404
    And the response should contain error message "Product not found"

  @api @data-driven
  Scenario Outline: Search products by category
    When I send a GET request to "/api/products?category=<category>"
    Then the response status code should be 200
    And all products should have category "<category>"
    And the result count should be <expected_count>

    Examples:
      | category    | expected_count |
      | Electronics | 5              |
      | Books       | 3              |
      | Clothing    | 10             |

  @api
  Scenario: Create product with data table
    When I create products with the following details:
      | name     | price  | category    |
      | Phone    | 599.99 | Electronics |
      | Tablet   | 399.99 | Electronics |
      | Headphones| 149.99| Electronics |
    Then all products should be created successfully
    And I should have 3 new products
