Feature: Customer Registration feature

  Scenario: Register Customer
    When a "CustomerRegistrationRequested" event for registering a customer is received
    Then the "CustomerCreated" customer event is sent
    And the customer gets a customer id

  Scenario: Deregister Customer
    Given a customer with name "Susan", last name "Doe", CPR "891291-111" and bank account "hgf234jj" is registered with DTU Pay
    When a "CustomerRegistrationRequested" event for deregistering a customer is received
    Then the "CustomerDeregistered" customer event is sent
    And the customer is not registered anymore

  Scenario: Validate Customer Account
    Given a customer with name "Susan", last name "Doe", CPR "891291-222" and bank account "fds234jj" is registered with DTU Pay
    When a "ValidateCustomerAccountRequested" event for validating a customer is received
    Then the "CustomerAccountValidated" customer event is sent
    And the customer account is valid
