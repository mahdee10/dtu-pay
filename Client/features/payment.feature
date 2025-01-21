Feature: Generate payment

  Scenario: Successful Payment
    Given customer with name "Susan", last name "Baldwin", and CPR "849291-1427"
    And customer is registered with the bank with an initial balance of 1000.0 kr
    And customer is registered with Simple DTU Pay using their bank account
    And merchant with name "Daniel", last name "Oliver", and CPR "898297-1647"
    And merchant is registered with the bank with an initial balance of 1000.0 kr
    And merchant is registered with Simple DTU Pay using their bank account
    And a customer token is requested by the merchant
    And the customer request 5 tokens
    When the merchant initiates a payment for 10 kr by the customer token
    Then the payment is successful
    And the customer has 4 tokens
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr