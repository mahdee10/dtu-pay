Feature: merchant
  Scenario:Register Merchant
When a merchant with name "Daniel", last name "Oliver", and CPR "891291-1641"
Then the merchant is registered with the bank with an initial balance of 1000.0 kr
And the merchant is registered with Simple DTU Pay using their bank account