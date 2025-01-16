Feature: Merchant Registration feature

  Scenario:Register Merchant
    When a "MerchantRegistrationRequested" event for a merchant is received
    Then The "MerchantCreated" event is sent
    And the merchant gets a merchant id
