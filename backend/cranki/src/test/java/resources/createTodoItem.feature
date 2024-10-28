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

Scenario: Create a New Todo (Normal Flow)

  Given there exists the following todos in the todo list named "Tasks"
    | id  | name        | description         | status      |
    | 0   | Buy milk    | Drink milk everyday | IN_PROGRESS |
    | 1   | Wash dishes | They're piling up   | DONE        |
  When requesting the creation of todo with name "Drink water" and description "Just do it" to the todo list "Tasks"
  Then the following todos exist in the todo list named "Tasks"
    | id  | name        | description         | status      |
    | 0   | Buy milk    | Drink milk everyday | IN_PROGRESS |
    | 1   | Wash dishes | They're piling up   | DONE        |
    | 2   | Drink water | Just do it          | NOT_DONE    |

Scenario: Attempt to Add a Todo with Empty Name (Error Flow)

  Given no todos have been created
  When requesting the creation of todo with name "" and description "Buy cookies" to the todo list "Groceries"
  Then the following error message is returned: "Cannot create todo with empty name"

Scenario: Attempt to Add a Todo with Duplicate Name (Error Flow)

  Given there exists the following todos in the todo list named "Tasks"
    | id  | name        | description          | status      |
    | 0   | Buy milk    | Drink milk everyday  | IN_PROGRESS |
    | 1   | Wash dishes | They're piling up    | DONE        |
  When requesting the creation of todo with name "Buy milk" and description "Just do it" to the todo list "Tasks"
  Then the following error message is returned: "Todo with the same name already exists"
