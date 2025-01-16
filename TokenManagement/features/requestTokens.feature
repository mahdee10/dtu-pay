#Author: your.email@your.domain.com
Feature: Requesting tokens 

  Scenario: Customer with more than one active token requests tokens
     Given a registered customer with id "123" with more than 1 active token
     When a registered customer with id "123" requests tokens
     Then an event "RequestTokensEvent" is sent
     When an event "RequestTokensResponse" is sent
     Then the response is unsuccessful
     And the request is denied
     
  Scenario: Customer with less than one active token requests less than 5 tokens
     Given a registered customer with id "123" with 1 or less active tokens
     When a registered customer with id "123" requests less than 5 tokens
     Then an event "RequestTokensEvent" is sent
     When an event "RequestTokensResponse"is sent
     Then the response is successful
     And the service will grant 3 active tokens to user
  
  Scenario: Customer requests more than the allowed amount of tokens
     Given a registered customer with id "123" with 1 or less active tokens
     When a registered customer with id "123" requests more than 5 tokens
     Then an event "RequestTokensEvent" is sent
     When an event "RequestTokensResponse" is sent
     Then response is unsuccessful
     And the request is denied
     