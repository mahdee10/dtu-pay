Feature: reporting

  Scenario: Manager requests report of all payments
    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1427" is registered with DTU Pay
    And merchant with name "Daniel", last name "Oliver", and CPR "192297-1647" is registered with DTU Pay
    And a list of payments are present in the repository
    When a manager requests all reports
    Then a report with all payments is returned to the manager

  Scenario: Merchant requests report of all payments
    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1427" is registered with DTU Pay
    And merchant with name "Daniel", last name "Oliver", and CPR "192297-1647" is registered with DTU Pay
    And a list of payments are present in the repository
    When merchant requests a report of all his payments
    Then a report with all payments made by the merchant is returned

  Scenario: Customer requests report of all payments
    Given customer with name "Susan", last name "Baldwin", and CPR "129291-1427" is registered with DTU Pay
    And merchant with name "Daniel", last name "Oliver", and CPR "192297-1647" is registered with DTU Pay
    And a list of payments are present in the repository
    When customer requests a report of all his payments
    Then a report with all payments made by the customer is returned
