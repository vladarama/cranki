Feature: Create a todo 

  As a Todo List User
  I would like to create a todo item
  so that I can be reminded of this task.

Background:
  Given the following todo lists exist
    | id | name      |
    | 0  | Groceries |
    | 1  | Chores    |
    | 2  | Tasks     |

Scenario Outline: Create a First Todo (Normal Flow)

  Given no todos have been created
  When requesting the creation of todo with name <name> and description <description> to the todo list <todo list>
  Then the todo with name <name> and description <description> exists with status "NOT_DONE" in the todo list <todo list>.

  Examples:
    | name           | description   | todo list   |
    | "Do groceries" | "Buy milk"    | "Groceries" |
    | "Do chores"    | "Wash dishes" | "Chores"    |

Scenario: Create a New Todo (Normal Flow)

  Given there exists the following todos
    | name        | description         | status      | todo list |
    | Buy milk    | Drink milk everyday | IN_PROGRESS | Tasks     |
    | Wash dishes | They're piling up   | DONE        | Tasks     |
  When requesting the creation of todo with name "Drink water" and description "Just do it" to the todo list "Tasks"
  Then the following todos exist
    | name        | description         | status      | todo list |
    | Buy milk    | Drink milk everyday | IN_PROGRESS | Tasks     |
    | Wash dishes | They're piling up   | DONE        | Tasks     |
    | Drink water | Just do it          | NOT_DONE    | Tasks     |

Scenario: Attempt to Add a Todo with Empty Name (Error Flow)

  Given no todos have been created
  When requesting the creation of todo with name "" and description "Buy cookies" to the todo list "Groceries"
  Then the following error message is returned: "Cannot create todo with empty name"

Scenario: Attempt to Add a Todo with Duplicate Name (Error Flow)

  Given there exists the following todos
    | name        | description         | status      | todo list |
    | Buy milk    | Drink milk everyday | IN_PROGRESS | Tasks     |
    | Wash dishes | They're piling up   | DONE        | Tasks     |
  When requesting the creation of todo with name "Buy milk" and description "Just do it" to the todo list "Tasks"
  Then the following error message is returned: "Todo with the same name already exists"
  
Scenario: Attempt to Add a Todo to a non-existing Todo List (Error Flow)

  Given there exists the following todos
    | name        | description         | status      | todo list |
    | Buy milk    | Drink milk everyday | IN_PROGRESS | Tasks     |
    | Wash dishes | They're piling up   | DONE        | Tasks     |
  When requesting the creation of todo with name "Buy milk" and description "Just do it" to the todo list "Workout"
  Then the following error message is returned: "The todo list does not exist"