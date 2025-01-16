#Author: your.email@your.domain.com

Feature: Issuing one token

  Scenario: Customer with at least one token tries to get one token issued
     Given a registered customer with id "123" with at least 1 token
     When the customer requests the token system to retreive a token
     Then the event "CustomerTokensRequest" is sent
     When the event "CustomerTokensReturned" is sent
     Then a client receives a token with token id "ID"

  Scenario: Customer with no tokens tries to get one token issued
     Given a registered customer with id "123" with 0 tokens
     When the customer requests the token system to retreive a token
     Then the event "CustomerTokensRequest" is sent
     When the event "CustomerTokensReturned" is sent
     Then the system throws an exception with message "You already have 5 unused tokens. Use them before requesting more."
