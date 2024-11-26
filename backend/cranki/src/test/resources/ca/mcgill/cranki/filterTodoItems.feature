Feature: Filter Todo Items
  As a user
  I would like to filter my todos by property
  So that I can easily find and focus on specific tasks

  Scenario: Filter todos by a specific property (Normal Flow)
    Given I have the following todos with property "Category":
      | Name               | Category   |
      | Buy groceries      | Personal   |
      | Finish project     | Work       |
      | Schedule doctor    | Personal   |
    When I select the "Personal" category filter
    Then only the todos "Buy groceries" and "Schedule doctor" should be displayed

  Scenario: Show all todos when no property filter is selected (Alternate Flow)
    Given I have the following todos with property "Category":
      | Name               | Category   |
      | Buy groceries      | Personal   |
      | Finish project     | Work       |
      | Schedule doctor    | Personal   |
    When no category filter is selected
    Then all todos should be displayed

  Scenario: Unable to filter because no property added (Error Flow)
    Given I have the following todos with no property added:
      | Name               |
      | Buy groceries      |
      | Finish project     |
      | Schedule doctor    |
    When the property filter list does not load
    Then the system should display an error message "Unable to load property filter list because no property added"
