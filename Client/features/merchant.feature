Feature: merchant
  Scenario:Register Merchant
    When a merchant with name "Daniel", last name "Oliver", and CPR "891291-1641"
    Then the merchant is registered with the bank with an initial balance of 1000.0 kr
    And the merchant is registered with DTU Pay using their bank account

  Scenario: Deregister Customer
    Given a merchant with name "Mark", last name "Oliver", and CPR "891291-2222"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with DTU Pay using their bank account
    When the merchant unregisters
    Then the merchant is not registered anymore
