#Author: your.email@your.domain.com

Feature: Token validation

  Scenario: A supplied active token is validated
     Given a payment service supplies a "valid" token with id "128e62be-42e7-42db-bb3a-9aab8db6e4d8" to validate
     When the event "TokenValidationRequest" is received
     Then a response event "TokenValidationReturned" is sent and contains the value "true"

  Scenario: A supplied inactive token is not validated
     Given a payment service supplies a "invalid" token with id "128e62be-42e7-42db-bb3a-9aab8db6e4d7" to validate
     When the event "TokenValidationRequest" is received
     Then a response event "TokenValidationReturned" is sent and contains the value "false"
     
  Scenario: A supplied non-existing token throws an exception
     Given a payment service supplies a "missing" token with id "128e62be-42e7-42db-bb3a-9aab8db6e4d6" to validate
     When the event "TokenValidationRequest" is received
     Then a response event "TokenValidationReturned" is sent and throws an exception "Token not found."

