Feature: customer
  Scenario: Customer registers
    When a customer with name "Susan", last name "Baldwin", and CPR "130554-4821"
    Then the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account

  Scenario: Deregister Customer
    Given a customer with name "Lisa", last name "Baldwin", and CPR "130554-2222"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    When the customer unregisters
    Then the customer is not registered anymore

  Scenario: Customer cannot request more tokens due to too many active tokens
    Given a customer with name "Anna", last name "Baldwin", and CPR "129291-1429"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has already 4 tokens
    When the customer requests to create 3 more tokens
    Then the the request is unsuccessful and the exception message "Too many active tokens" is returned

  Scenario: Customer cannot get tokens issued due to no tokens
    Given a customer with name "Maria", last name "Baldwin", and CPR "155691-1430"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with DTU Pay using their bank account
    And the customer has already 0 tokens
    When the customer requests to get tokens
    Then the the request is unsuccessful and the exception message "You have no more tokens. Request more tokens." is returned
