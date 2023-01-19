Feature: Self Serv Login

  Scenario: Login to an existing account
    Given A web browser is at the Self Serv login page
    When User enter "madhawa@outlook.com" for the username
    And User enter "password" for the password
    And User click Sign In button
    Then User should be logged in