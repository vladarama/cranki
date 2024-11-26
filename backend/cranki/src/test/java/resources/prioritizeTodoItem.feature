Feature: Prioritize Todo Items

As a user
I would like to order my todos by priority
So that I can focus on the most important tasks first

Background:
    Given the following todo list exists for the user:
        | id | name   |
        | 1  | School |

Scenario: Add a priority to a new todo (Normal Flow)
    Given the following todos exist:
        | name             | priority | todo list    |
        | COMP Assignment  | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | Essay            | LOW      | School       |
    When the user creates a new todo with the following details:
        | name         | priority | todo list    |
        | Presentation | HIGH     | School       |
    Then the new todo is added to the list with the following order and priorities:
        | name             | priority | todo list    |
        | COMP Assignment  | HIGH     | School       |
        | Presentation     | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | Essay            | LOW      | School       |

Scenario: Change the priority of an existing todo (Alternate Flow)
    Given the following todos exist:
        | name             | priority | todo list    |
        |COMP Assignment  | HIGH     | School       |
        | Presentation     | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | Essay            | LOW      | School       |
    When the user requests to set the task with name "COMP Assignment" to "LOW" priority:
    Then the todo list is updated with the following order and priorities:
        | name             | priority | todo list    |
        | Presentation     | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | COMP Assignment  | LOW      | School       |
        | Essay            | LOW      | School       |

Scenario: Add a new todo without specifying a priority level (Alternate Flow)
    Given the following todos exist:
        | name             | priority | todo list    |
        | Presentation     | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | COMP Assignment  | LOW      | School       |
        | Essay            | LOW      | School       |
    When the user creates a new todo with the following details:
        | name         | priority | todo list    |
        | Lab report   |          | School       |
    Then the new todo is added to the list with the following order and priorities:
        | name             | priority | todo list    |
        | Presentation     | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | COMP Assignment  | LOW      | School       |
        | Essay            | LOW      | School       |
        | Lab report       | LOW      | School       |
        
Scenario: Change the priority of a non-existing todo (Error Flow)
    Given the following todos exist:
        | name             | priority | todo list    |
        | Presentation     | HIGH     | School       |
        | DPM report       | MEDIUM   | School       |
        | COMP Assignment  | LOW      | School       |
        | Essay            | LOW      | School       |
    When the user requests to set the task with name "ECSE Assignment" to "LOW" priority:
    Then the error message "Task not found" is returned