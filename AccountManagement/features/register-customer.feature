Feature: Customer Registration feature

  Scenario: Register Customer
    When a "CustomerRegistrationRequested" event for a customer is received
    Then the "CustomerCreated" event is sent
    And the customer gets a customer id
