Feature: Edit Todo
    As a User 
    I would like to edit a todo item
    So that I can update its name

Background:
    Given the following todo exists
        | id | name          |
        | 1  | Buy Groceries |

Scenario: User edits the name of the todo (Normal Flow)
    When I edit the todo item name to "Buy Milk"
    Then the todo item name should be "Buy Milk"

Scenario: User attempts to edit with empty name (Error Flow)
    When I try to edit the todo item name to empty
    Then I should receive an empty name error

Scenario: User attempts to edit non-existent todo (Error Flow)
    When I try to edit a non-existent todo item with id "999"
    Then I should receive a not found error