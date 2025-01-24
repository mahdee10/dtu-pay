Feature: Merchant Registration feature

  Scenario:Register Merchant
    When a "MerchantRegistrationRequested" event for registering a merchant is received
    Then the "MerchantCreated" merchant event is sent
    And the merchant gets a merchant id

  Scenario: Deregister Merchant
    Given a merchant with name "Daniel", last name "Oliver", CPR "651291-000" and bank account "gxz234jj" is registered with DTU Pay
    When a "MerchantDeregistrationRequested" event for deregistering a merchant is received
    Then the "MerchantDeregistered" merchant event is sent
    And the merchant is not registered anymore

  Scenario: Validate Merchant Account
    Given a merchant with name "Daniel", last name "Oliver", CPR "8651291-111" and bank account "dgs234jj" is registered with DTU Pay
    When a "ValidateMerchantAccountRequested" event for validating a merchant is received
    Then the "MerchantAccountValidated" merchant event is sent
    And the merchant account is valid

