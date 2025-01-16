#Author: your.email@your.domain.com
Feature: Requesting tokens 

  Scenario: Client with more than one token requests tokens
     Given a registered client with more than 1 token
     When requests 5 or less tokens
     Then the request is denied
     
  Scenario: Client with less than one token requests tokens
     Given a registered client with 1 or less tokens
     When a client requests 3 tokens
     And 3 is between 1 and 5
     Then the service will grant 3 active tokens to user
  
  Scenario: Client requests more than 5 tokens
     Given a registered client with 1 or less tokens
     When a client requests 6 tokens
     And 6 more than 5
     Then the request is denied