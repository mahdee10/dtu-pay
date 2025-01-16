#Author: your.email@your.domain.com

Feature: Using a token

  Scenario: A valid token is used to pay
     Given a payment service supplies a valid token with UUID "UUID" to pay
     When an event "UseTokenRequest" is sent
     Then a response "UseTokenResponse" is sent
     When the response is successful
     Then a token with id "ID" is marked as invalid
     And a token with id "ID" is removed from the active token list and placed in an inactive token list
