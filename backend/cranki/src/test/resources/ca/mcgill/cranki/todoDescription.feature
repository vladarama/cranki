Feature: Add description to todo item

    As a user
    I want to add a description to a todo item
    So that I can provide more details about the task

    Background:
        Given the following todo lists exist in the system
            | id | name          |
            | 1  | House Keeping |

    Scenario: User adds a description to an existing todo item (Normal Flow)
        Given I have an existing todo item "Buy groceries" with no description:
        When I add a description "Need banana and eggplants" to the todo item
        Then the todo item "Buy groceries" should have the description "Need banana and eggplants"

    Scenario: User edits the description of an existing todo item (Alternate Flow)
        Given I have an existing todo item "Buy groceries" with description "Need banana and eggplants":
        When I change the description to "Kill all the mushrooms"
        Then the todo item "Buy groceries" should have the updated description "Kill all the mushrooms"

    Scenario: User removes the description from an existing todo item (Alternate Flow)
        Given I have an existing todo item "Buy groceries" with description "Need banana and eggplants":
        When I remove the description from the todo item
        Then the todo item "Buy groceries" should have no description

    Scenario: User tries to add a description to an existing todo item that exceeds the maximum length (Error Flow)
        Given I have an existing todo item "Buy groceries" with no description:
        When I add a description that exceeds 2000 characters to the todo item
        Then I should get an error message "Description exceeds maximum length of 2000 characters"
    
