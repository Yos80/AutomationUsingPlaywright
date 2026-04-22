Feature: SSA Landing Page
  As a visitor to ssa.gov
  I want to verify the landing page displays correctly and core navigation works
  So that users can find information and sign in to their accounts

  Background:
    Given I am on the SSA home page

  Scenario: Page title reflects the Social Security Administration
    Then the page title should contain "Social Security"

  Scenario: Primary navigation buttons are visible
    Then I should see the "Benefits" navigation button
    And I should see the "Medicare" navigation button
    And I should see the "Card & record" navigation button

  Scenario: Sign-in call to action is visible on the page
    Then a "Sign in or create an account" link should be visible

  Scenario: Main hero heading is displayed
    Then the main heading should read "Your most-needed services, online"

  Scenario: Searching for a topic navigates to results
    When I search for "retirement benefits"
    Then the search results page should be displayed
