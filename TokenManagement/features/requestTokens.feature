Feature: Requesting tokens

  Scenario: Customer with more than one active token requests tokens
     Given an existing registered customer with id "256f6d23-8c12-47a4-ae02-0f5c3c957f62" with more than 1 active token
     When a registered customer with id "256f6d23-8c12-47a4-ae02-0f5c3c957f62" requests 3 tokens and an event RequestTokensEvent "CreateTokensRequested" is sent
     Then a response RequestTokensResponse "ResponseTokensCreated" is sent and throws and exception "Too many active tokens"

  Scenario: Customer with less than one active token requests up to 5 tokens
     Given an existing registered customer with id "aefcbde7-2133-4581-b43c-468c3247f2b0" with 1 or less active tokens
     When a registered customer with id "aefcbde7-2133-4581-b43c-468c3247f2b0" requests 3 tokens an event RequestTokensEvent "CreateTokensRequested" is sent
     Then a response RequestTokensResponse "ResponseTokensCreated" is sent containing a list with 3 new tokens
     And a customer with id "aefcbde7-2133-4581-b43c-468c3247f2b0" has 4 active tokens

  Scenario: Customer requests more than the allowed amount of tokens
     Given an existing registered customer with id "7b137b1e-de3e-48e7-ac96-2f6e4ab7a04b" with 1 or less active tokens
     When a registered customer with id "7b137b1e-de3e-48e7-ac96-2f6e4ab7a04b" requests 6 tokens and an event RequestTokensEvent "CreateTokensRequested" is sent
     Then a response RequestTokensResponse "ResponseTokensCreated" is sent and throws an exception "Too many tokens requested"
