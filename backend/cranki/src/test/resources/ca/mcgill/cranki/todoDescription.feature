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
            | id | name          | Description | list_id |
            | 1  | Buy groceries |             | 1       |
        When I add a description "Need banana and eggplants" to the todo item
        Then the todo item "Read book" should have the description "Need banana and eggplants"
            | id | name          | Description                 | list_id |
            | 1  | Buy groceries | "Need banana and eggplants" | 1       |

    Scenario: User edits the description of an existing todo item (Alternate Flow)
        Given I have an existing todo item "Buy groceries" with description "Need banana and eggplants":
            | id | name          | Description                 | list_id |
            | 1  | Buy groceries | "Need banana and eggplants" | 1       |
        When I change the description to "Kill all the mushrooms"
        Then the todo item "Buy groceries" should have the updated description "Kill all the mushrooms"
            | id | name          | Description                 | list_id |
            | 1  | Buy groceries | "Kill all the mushrooms"    | 1       |

    Scenario: User removes the description from an existing todo item (Alternate Flow)
        Given I have an existing todo item "Buy groceries" with description "Need banana and eggplants":
            | id | name          | Description                 | list_id |
            | 1  | Buy groceries | "Need banana and eggplants" | 1       |
        When I remove the description from the todo item with id 1
        Then the todo item "Buy groceries" should have no description
            | id | name          | Description | list_id |
            | 1  | Buy groceries |             | 1       |

    Scenario: User tries to add a description to an existing todo item that exceeds the maximum length (Error Flow)
        Given I have no todo items
        When I add a description "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod..." that exceeds 2000 characters to the todo item
        Then I should see an error message "Description exceeds maximum length of 2000 characters"
    
