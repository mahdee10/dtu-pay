Feature: Payment Service feature

  Scenario: Merchant requests a payment
    When "PaymentRequested" event to execute a payment is received
    Then the payment is executed and the "PaymentCompleted" event is sent
    And the payment confirmation is received by the merchant

  Scenario: Manager gets payment list
    Given a list of payments are present in the payment repository
    When "GetPaymentsRequested" event to get all payments is received
    Then the payments are fetched and the "PaymentsFetched" event is sent
    And the user gets the list of payments

  Scenario: Customer gets payment list
    Given a list of payments are present in the payment repository for customer "c1beb042-5198-4104-9062-9b48c4abc87c"
    When "GetCustomerPaymentsRequested" event to get all the customer payments is received for customer "c1beb042-5198-4104-9062-9b48c4abc87c"
    Then the payments are fetched and the "CustomerPaymentsFetched" event is sent
    And the user gets the list of payments

  Scenario: Merchant gets payment list
    Given a list of payments are present in the payment repository for merchant "f11decde-0519-46ec-87c1-aa57df4d7b3f"
    When "GetMerchantPaymentsRequested" event to get all the merchant payments is received for merchant "f11decde-0519-46ec-87c1-aa57df4d7b3f"
    Then the payments are fetched and the "MerchantPaymentsFetched" event is sent
    And the user gets the list of payments
