Feature: customer
  Scenario: Register Customer
    When a customer with name "Susans", last name "Baldwin", and CPR "130554-4821"
    Then the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account



