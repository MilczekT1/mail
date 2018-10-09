Feature: Sending user activation email

  Scenario: Authorized user can send sign up confirmation
    Given I'm authenticated with Basic Auth
    When I send sign up confirmation email with activation link to email@email.com
    Then email with sign up confirmation is sent

  Scenario: Unauthorized user can't send sign up confirmation
    Given I'm not authenticated
    When I send sign up confirmation email with activation link to email@email.com
    Then the operation is unauthorized
