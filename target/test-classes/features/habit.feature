Feature: Habit Management API
  As a user
  I want to manage my habits
  So that I can track my daily goals

  Scenario: Create a new habit
    Given the habit repository is empty
    When I send a POST request to "/api/habits" with name "Read Book" and description "Read 20 pages"
    Then the response status should be 201
    And the response should contain a habit named "Read Book"

  Scenario: Retrieve all habits
    Given the following habits exist:
      | name      | description |
      | Exercise  | 30 min run  |
      | Meditate  | 10 min      |
    When I send a GET request to "/api/habits"
    Then the response status should be 200
    And the response should contain 2 habits