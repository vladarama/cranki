Feature: Create a todo 

As a Todo List User
I would like to create a todo item
so that I can be reminder of this task.

Scenario Outline: Create a First Todo (Normal Flow)

Given no todos have been created
When requesting the creation of todo <id> with name <name>
Then the todo <id> with name <name> and state Incomplete exists. 

| id  | name          | 
| 0   | "Buy milk"    |
| 1   | "Wash dishes" |


Scenario Outline: Create a New Todo (Normal Flow)

Given there exists todos with id <id> and name <name>
| id  | name          | 
| 0   | "Buy milk"    |
| 1   | "Wash dishes" |
When requesting the creation of todo <id> with name <name>
Then the todo <id> with name <name> and state Incomplete exists. 
| id  | name                             | 
| 2   | "Buy milk"            |
| 3   | "Put dishes in strawberry milk." |


Scenario Outline: Attempt to Add a Todo with Empty Name (Error Flow)

Given no todos have been created
When requesting the creation of todo with id 0 and name "" 
Then the following error message is returned: "Error: Cannot create todo with empty name.". 


Scenario Outline: Attempt to Add a Todo with Duplicate id (Error Flow)

Given there exists todos with id <id> and name <name>
| id  | name          | 
| 0   | "Buy milk"    |
| 1   | "Wash dishes" |
When requesting the creation of todo <id> with name <name>
Then the following error message is returned: "Error: cannot create a todo with duplicate ID.". 

