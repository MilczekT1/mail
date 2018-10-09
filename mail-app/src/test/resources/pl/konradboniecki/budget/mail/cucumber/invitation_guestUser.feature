Feature: Sending Invitation to family for guest user

  Background:
    Given I'm authenticated with Basic Auth
    And I have a family

  Scenario: Authorized user can send invitation to guest user
    Given I'm authenticated with Basic Auth
    When I invite guest user with email email@email.com to my family
    Then email with invitation is sent

  Scenario: Unauthorized user can't can send invitation to guest user
    Given I'm not authenticated
    When I invite guest user with email email@email.com to random family
    Then the operation is unauthorized
