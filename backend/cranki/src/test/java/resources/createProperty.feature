Feature: Create a todo 

  As a Todo List User
  I would like to create a property
  so that I have finer grained control over my todo list.

  Background:
    Given the following todo lists exist
      | id | name      |
      | 1  | Chores    |
      | 2  | Tasks     |
    And the following literal properties exist
      | id | name                | todoListId         |
      | 1  | My Literal Property | 1                  |
      | 2  | Corresponding Duck  | 2                  |
    And the following multiselect properties exist
      | id | name                | todoListId | valueIds |
      | 3  | Cheese              | 1          | 1;2;3    |

  Scenario: User creates a literal property (Normal Flow)
    When the user creates a literal property with name "new lit prop" for todoList with id "1"
    Then the following literal properties exist
      | name                | todoListId         |
      | My Literal Property | 1                  |
      | Corresponding Duck  | 2                  |
      | new lit prop        | 1                  |

    Scenario: User fails to create a literal property for a todo list that does not exist (Error Flow)
      When the user creates a literal property with name "bleh" for todo list with id "42"
      Then a list does not exist error shall be thrown


  Scenario: User creates a multiselect property (Alternate Flow)
    When the user creates a multiselect property with name "new ms prop" for todoList with id "1"
    Then the following multiselect properties exist
      | name                | todoListId | values                |
      | Cheese              | 1          | cheddar;brie;parmesan |
      | new ms prop         | 1          |                       |

  Scenario: User fails to create a multiselect property for a todo list that does not exist (Error Flow)
    When the user creates a multiselect property with name "blarg" for todo list with id "42"
    Then a list does not exist error shall be thrown
