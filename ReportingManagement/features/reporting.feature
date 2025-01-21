Feature: ReportingService functionality
  As a user
  I want to test the reporting service
  So that I can verify payments are correctly fetched and stored.

  Scenario: Fetch all payments
    Given the reporting service is running
    When I request all payments
    Then I should see all payments stored in the repository

  Scenario: Fetch payments for a customer
    Given the reporting service is running
    And a customer with ID "7911f9a4-440f-41b5-ae69-1082ddc7be69" exists
    When I request payments for the customer
    Then I should see payments for the customer stored in the repository

  Scenario: Fetch payments for a merchant
    Given the reporting service is running
    And a merchant with ID "5a51e254-e9bf-4762-81d7-eeadf10347b6" exists
    When I request payments for the merchant
    Then I should see payments for the merchant stored in the repository
