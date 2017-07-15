Feature: Already implemented.

  Scenario: get node ip
    When execute GET /node/ip
    Then the response body is equal to '127.0.0.1'
