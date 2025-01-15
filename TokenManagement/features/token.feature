Feature: Token creation feature

  Scenario: Token creation
  	Given there is a student with empty id
  	When the student is being registered
  	Then the "StudentRegistrationRequested" event is sent
  	When the "StudentIdAssigned" event is sent with non-empty id
  	Then the student is registered and his id is set
