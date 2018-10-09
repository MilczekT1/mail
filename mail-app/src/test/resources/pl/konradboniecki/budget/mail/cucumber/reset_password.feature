Feature: Sending reset password email

  Scenario: Authorized user can send email with reset password link
    Given I'm authenticated with Basic Auth
    When I send email with reset password link to email@email.com
    Then email with reset password link is sent

  Scenario: Unauthorized user can't send email with reset password link
    Given I'm not authenticated
    When I send email with reset password link to email@email.com
    Then the operation is unauthorized
