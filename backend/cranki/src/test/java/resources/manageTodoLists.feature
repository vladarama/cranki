Feature: Manage a Todo List

As a user of the Cranki application,
I want to create, update, and delete to-do lists,
So that I can organize and manage my to-do items effectively.

Background:
    Given the following to-do lists exist
    | id | name     |
    | 1  | Personal | 
    | 2  | Work     | 
    | 3  | School   | 


Scenario: Create a new todo list (Normal Flow)
    When the user creates a new to-do list with the name <name>
    Then the new to-do list <name> is created with the ID <id>
    | id | name     |
    | 4  | Fitness  | 


Scenario: Create a duplicate todo list (Error Flow)
    Given a to-do list <name> already exists
    When the user attempts to create a new to-do list with the name <name>
    Then an error message "List with name <name> already exists" is displayed
    And no new list is created
    | id | name |
    | 5  | Fun  | 


Scenario: Create a todo list without a name (Error Flow)
    When the user attempts to create a to-do list without providing a name
    Then an error message "List name is required" is displayed
    And no new to-do list is created


Scenario: Update an existing to-do list (Alternate Flow)
    When the user updates the name of the to-do list with ID <id> to <new_name>
    Then the to-do list with ID <id> is updated to <new_name>
    And the updated to-do list <new_name> is available on the dashboard
    | id | name     |
    | 2  | new_name | 


Scenario: User cancels the update operation (Alternate Flow)
    Given the user is in the process of updating the to-do list with ID <id>
    When the user cancels the update
    Then the to-do list retains its original name <name>
    And no changes are made to the to-do list
    | id | name     |
    | 2  | old_name | 


Scenario: Delete an existing to-do list (Alternate Flow)
    When the user deletes the to-do list with ID <id>
    Then the to-do list with ID <id> is removed
    And the to-do list with ID <id> is no longer available on the dashboard


Scenario: Delete a non-existing to-do list (Error Flow)
    When the user attempts to delete a non-existing to-do list with ID <id>
    Then an error message "List with ID <id> does not exist" is displayed
    And no deletion is made