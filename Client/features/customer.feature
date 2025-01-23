Feature: customer
  Scenario: Customer registers
    When a customer with name "Susan", last name "Baldwin", and CPR "130554-4821"
    Then the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account

  Scenario: Deregister Customer
    Given a customer with name "Lisa", last name "Baldwin", and CPR "130554-2222"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account
    When the customer unregisters
    Then the customer is not registered anymore
#
#  Scenario: Validate Customer Account
#    Given a customer with name "Susan", last name "Doe", CPR "891291-222" and bank account "fds234jj" is registered with DTU Pay
#    When a "ValidateCustomerAccountRequested" event for validating a customer is received
#    Then the "CustomerAccountValidated" customer event is sent
#    And the customer account is valid

#  Scenario: Customer cannot request more tokens due to too many active tokens
#    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1429"
#    And customer is registered with the bank
#    And customer is registered with Simple DTU Pay using their bank account
#    And the customer has 2 tokens
#    And the customer requests 3 tokens
#    When the customer requests 3 tokens
#    Then the the request is unsuccessful and the exception message "Too many active tokens" is returned
#    And and the customer retains 2 tokens
#
#  Scenario: Customer cannot get tokens issued due to no tokens
#    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1429"
#    And customer is registered with the bank
#    And customer is registered with Simple DTU Pay using their bank account
#    And the customer has 0 tokens
#    And the customer attempts to get a token to send a merchant for payment
#    When the customer attempts to get a token
#    Then the the request is unsuccessful and the exception message "You have no more tokens. Request more tokens." is returned
#    And and the customer has 0 tokens
#
#  Scenario: Customer requests report of all payments where customer is involved
#    When a registered DTU pay customer with id "bccd8134-648b-428e-93c1-c5f7a1eb660f" exists
#    And a customer with id "bccd8134-648b-428e-93c1-c5f7a1eb660f" requests a report
#    Then then the customer with id "bccd8134-648b-428e-93c1-c5f7a1eb660f" receives a report of all their payments
#    And all payments in the report contain customer id "bccd8134-648b-428e-93c1-c5f7a1eb660f"
#
#
#
#
#
