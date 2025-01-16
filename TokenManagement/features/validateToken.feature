#Author: your.email@your.domain.com

Feature: Token validation

  Scenario: A supplied active token is validated
     Given a payment service supplies a token with id "ID" to validate
     When a token with id "ID" exists and is active
     And token validation is successful


  Scenario: A supplied inactive token is not validated
     Given a payment service supplies a token with id "ID" to validate
     When a token with id "ID" exists but is inactive
     And token validation is not successful
     
  Scenario: A supplied non-existing token throws an exception
     Given a payment service supplies a token with "ID" to validate
     When a token with id "ID" does not exist
     Then an exception is thrown

