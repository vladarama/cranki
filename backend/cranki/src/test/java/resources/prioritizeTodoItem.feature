Feature: Prioritize Todo Items

As a user
I would like to order my todos by priority
So that I can focus on the most important tasks first

Background:
    Given the following todo list exists for the user:
        | id | Name   |
        | 1  | School |

Scenario: Add a priority to a new todo (Normal Flow)
    Given the following todos exist in the School todo list:
        | id | Name             | Priority | list_id |
        | 1  | COMP Assignment  | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 3  | Essay            | low      | 1       |
    When the user creates a new todo with the following details:
        | id | Name         | Priority | list_id |
        | 4  | Presentation | high     | 1       |
    Then the new todo is added to the list with the following order and priorities:
        | id | Name             | Priority | list_id |
        | 1  | COMP Assignment  | high     | 1       |
        | 4  | Presentation     | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 3  | Essay            | low      | 1       |

Scenario: Change the priority of an existing todo (Alternate Flow)
    Given the following todos exist in the School todo list:
        | id | Name             | Priority | list_id |
        | 1  | COMP Assignment  | high     | 1       |
        | 4  | Presentation     | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 3  | Essay            | low      | 1       |
    When the user requests to set the task with id 1 to low priority:
    Then the todo list is updated with the following order and priorities:
        | id | Name             | Priority | list_id |
        | 4  | Presentation     | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 1  | COMP Assignment  | low      | 1       |
        | 3  | Essay            | low      | 1       |

Scenario: Add a new todo without specifying a priority level (Alternate Flow)
    Given the following todos exist in the School todo list:
        | id | Name             | Priority | list_id |
        | 4  | Presentation     | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 1  | COMP Assignment  | low      | 1       |
        | 3  | Essay            | low      | 1       |
    When the user creates a new todo with the following details:
        | id | Name         | Priority | list_id |
        | 5  | Lab report   |          | 1       |
    Then the new todo is added to the list with the following order and priorities:
        | id | Name             | Priority | list_id |
        | 4  | Presentation     | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 1  | COMP Assignment  | low      | 1       |
        | 3  | Essay            | low      | 1       |
        | 5  | Lab report       | low      | 1       |

Scenario: Change the priority of a non-existing todo (Error Flow)
    Given the following todos exist in the School todo list:
        | id | Name             | Priority | list_id |
        | 4  | Presentation     | high     | 1       |
        | 2  | DPM report       | medium   | 1       |
        | 1  | COMP Assignment  | low      | 1       |
        | 3  | Essay            | low      | 1       |
    When the user requests to set the task with id 5 to high priority:
    Then the error message "Task not found" is returned