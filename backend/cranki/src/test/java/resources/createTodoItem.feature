Feature: Create a todo 

  As a Todo List User
  I would like to create a todo item
  so that I can be reminded of this task.

Scenario Outline: Create a First Todo (Normal Flow)

  Given no todos have been created
  When requesting the creation of todo with name <name> and description <description> to the todo list <task list>
  Then the todo with name <name> and description <description> exist with status "NOT_DONE" in the todo list <task list>.

  Examples:
    | name           | description   | task list   |
    | "Do groceries" | "Buy milk"    | "Groceries" |
    | "Do chores"    | "Wash dishes" | "Chores"    |

Scenario Outline: Create a New Todo (Normal Flow)

Given there exists todos with id <id> and name <name>
Examples:
| id  | name          | 
| 0   | "Buy milk"    |
| 1   | "Wash dishes" |
When requesting the creation of todo <id> with name <name>
Then the todo <id> with name <name> and state Incomplete exists.
Examples:
| id  | name                             | 
| 2   | "Buy milk"            |
| 3   | "Put dishes in strawberry milk." |

Scenario: Attempt to Add a Todo with Empty Name (Error Flow)

  Given no todos have been created
  When requesting the creation of todo with name "" and description "Buy cookies" to the todo list "Groceries"
  Then the following error message is returned: "Cannot create todo with empty name."

Scenario: Attempt to Add a Todo with Duplicate Name (Error Flow)

  Given there exists todos with id <id> and name <name>
  When requesting the creation of todo <id> with name <name>
  Then the following error message is returned: "Error: cannot create a todo with duplicate ID.".
