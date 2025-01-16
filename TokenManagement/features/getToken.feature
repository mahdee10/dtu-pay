#Author: your.email@your.domain.com

Feature: Issuing one token

  Scenario: Client with at least one token tries to get one token issued
     Given a registered client with at least 1 token
     When the client requests the token system to retreive a token
     Then a client receives a token with token id "ID"

  Scenario: Client with no tokens tries to get one token issued
     Given a registered client with 0 tokens
     When the client requests the token system to retreive a token
     Then the system throws an exception
