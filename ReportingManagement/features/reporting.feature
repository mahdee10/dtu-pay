Feature: ReportingService functionality
  As a user
  I want to test the reporting service
  So that I can verify payments are correctly fetched and stored.

  Scenario: Fetch all payments
    Given a list of payments are present in the payment repository
    When "ReportingGetPaymentsRequested" event to get all payments is received
    Then the payments are fetched and the "ReportingGetPaymentsFetched" event is sent
    And the user gets the list of payments

  Scenario: Fetch payments for a customer
    Given a list of payments are present in the payment repository for customer with ID "7911f9a4-440f-41b5-ae69-1082ddc7be69"
    When "ReportingGetCustomerPaymentsRequested" event to get all payments is received
    Then the payments are fetched and the "ReportingCustomerPaymentsFetched" event is sent
    And the user gets the list of payments

  Scenario: Fetch payments for a merchant
    And  a list of payments are present in the payment repository for merchant with ID "5a51e254-e9bf-4762-81d7-eeadf10347b6"
    When "ReportingGetCustomerPaymentsRequested" event to get all payments is received
    Then the payments are fetched and the "ReportingCustomerPaymentsFetched" event is sent
    And the user gets the list of payments
