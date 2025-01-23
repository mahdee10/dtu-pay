Feature: merchant
  Scenario:Register Merchant
    When a merchant with name "Daniel", last name "Oliver", and CPR "891291-1641"
    Then the merchant is registered with the bank with an initial balance of 1000.0 kr
    And the merchant is registered with Simple DTU Pay using their bank account

  Scenario: Deregister Customer
    Given a merchant with name "Mark", last name "Oliver", and CPR "891291-2222"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with Simple DTU Pay using their bank account
    When the merchant unregisters
    Then the merchant is not registered anymore

#  Scenario: Merchant requests report of all payments where merchant is involved
#    When a registered DTU pay merchant with merchant id "c47d5176-c6b0-49f5-b431-5a73ebcce9b4" exists
#    And a merchant with id "c47d5176-c6b0-49f5-b431-5a73ebcce9b4" requests a report
#    Then then the merchant with id "c47d5176-c6b0-49f5-b431-5a73ebcce9b4" receives a report of all their payments
#    And all payments in the report contain merchant id "bccd8134-648b-428e-93c1-c5f7a1eb660f"
