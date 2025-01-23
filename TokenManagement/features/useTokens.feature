# use and Validate tokens

Feature: Using a token

  Scenario: A valid token is used to pay
    Given a payment service supplies a token with UUID "128e62be-42e7-42db-bb3a-9aab8db6e4d9" to pay
    And a registered DTU pay customer with UUID "49e3d00a-b113-460b-9a9e-71f7a4376ce8" is associated with the token
    And the token is valid
    When the event "UseTokenRequest" is received
    Then a response "UseTokenResponse" is sent and contains the customer UUID "49e3d00a-b113-460b-9a9e-71f7a4376ce8"
    And a response contains the token UUID "128e62be-42e7-42db-bb3a-9aab8db6e4d9"

  Scenario: An already used token is used to pay
    Given a payment service supplies a token with UUID "bcc761ab-40e5-42ab-8c04-3b1836ddfb7c" to pay
    And a registered DTU pay customer with UUID "49e3d00a-b113-460b-9a9e-71f7a4376ce8" is associated with the token
    And the token has been used and is invalid
    When the event "UseTokenRequest" is received
    Then a response event "UseTokenResponse" is sent and contains an exception "Token does not exist"

  Scenario: A non-existing token is used to pay
    Given a payment service supplies an token with UUID "36974c1f-c4db-4418-8a3f-e62e78a5x1305" to pay
    When the event "UseTokenRequest" is received
    Then a response event "UseTokenResponse" is sent and contains an exception "Token does not exist"
