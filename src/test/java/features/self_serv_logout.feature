Feature: Self Serv Logout

  Scenario: Logout from Self Serv
    Given User has logged in to Self Serv
    When User click Logout button
    Then User should logged out
    And User should be in Sign In page