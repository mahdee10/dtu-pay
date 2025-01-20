#Author: your.email@your.domain.com

Feature: Using a token

  Scenario: A valid token is used to pay
     Given a payment service supplies a valid token with UUID "128e62be-42e7-42db-bb3a-9aab8db6e4d9" to pay
     When the event "UseTokenRequest" is received
     Then a response event "UseTokenResponse" is sent and contains the value "true"
