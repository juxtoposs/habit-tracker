Feature: Habit Management API
  As a user
  I want to manage my habits
  So that I can track my daily goals

  Scenario: Discover API root
    Given the habit repository is empty
    When I send a GET request to "/api"
    Then the response status should be 200
    And the response should contain a link named "self"
    And the response should contain a link named "habits"

  Scenario: Create a new habit
    Given the habit repository is empty
    When I send a POST request to "/api/habits" with name "Read Book" and description "Read 20 pages"
    Then the response status should be 201
    And the response should contain a habit named "Read Book"
    And the response should contain a link named "self"
    And the response should contain a link named "logs"

  Scenario: Reject invalid habit input
    Given the habit repository is empty
    When I send a POST request to "/api/habits" with name "" and description "Invalid habit"
    Then the response status should be 400
    And the response should contain a validation error for field "name"

  Scenario: Retrieve all habits
    Given the following habits exist:
      | name     | description |
      | Exercise | 30 min run  |
      | Meditate | 10 min      |
    When I send a GET request to "/api/habits"
    Then the response status should be 200
    And the response should contain 2 habits
    And the response should contain a link named "self"
    And the response should contain a link named "create-habit"

  Scenario: Mark a habit as completed
    Given a habit named "Read Book" exists with description "Read 20 pages"
    When I send a POST request to complete habit "Read Book" on "2026-06-12"
    Then the response status should be 201
    And the response should contain completed date "2026-06-12"
    And the response should contain a link named "self"
    And the response should contain a link named "habit"

  Scenario: Prevent duplicate completion log
    Given a habit named "Read Book" exists with description "Read 20 pages"
    And habit "Read Book" was completed on "2026-06-12"
    When I send a POST request to complete habit "Read Book" on "2026-06-12"
    Then the response status should be 409

  Scenario: Prevent deleting a log through the wrong habit URL
    Given a habit named "Read Book" exists with description "Read 20 pages"
    And a habit named "Exercise" exists with description "Exercise daily"
    And habit "Read Book" was completed on "2026-06-12"
    When I send a DELETE request for the latest log of "Read Book" through habit "Exercise"
    Then the response status should be 404
    When I send a GET request for logs of habit "Read Book"
    Then the response status should be 200
    And the response should contain completed date "2026-06-12"

  Scenario: Use ETag cache validation for habit list
    Given the following habits exist:
      | name     | description |
      | Exercise | 30 min run  |
    When I send a GET request to "/api/habits"
    Then the response status should be 200
    And the response header "ETag" should be present
    And the response header "Cache-Control" should contain "max-age"
    When I send a GET request to "/api/habits" with If-None-Match from the previous response
    Then the response status should be 304