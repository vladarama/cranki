Feature: Create a todo 

  As a Todo List User
  I would like to create a property
  so that I have finer grained control over my todo list.

  Background:
    Given the following todo lists exist2
      | id | name      |
      | 1  | Chores    |
      | 2  | Tasks     |
    And the following literal properties already exist
      | id | name                | todoListName         |
      | 1  | My Literal Property | Chores               |
      | 2  | Corresponding Duck  | Tasks                |
    And the following multiselect properties already exist
      | id | name                | todoListName | valueIds |
      | 3  | Cheese              | Chores       | 1;2;3    |

  Scenario: User creates a literal property (Normal Flow)
    When the user creates a literal property with name "new lit prop" for todo list with name "Chores"
    Then the following literal properties shall exist
      | id | name                | todoListName       |
      | 1  | My Literal Property | Chores             |
      | 2  | Corresponding Duck  | Tasks              |
      | 4  | new lit prop        | Chores             |

    Scenario: User fails to create a literal property for a todo list that does not exist (Error Flow)
      When the user creates a literal property with name "bleh" for todo list with name "ungabunga"
      Then a list does not exist error shall be thrown


  Scenario: User creates a multiselect property (Alternate Flow)
    When the user creates a multiselect property with name "new ms prop" for todo list with id "1"
    Then the following multiselect properties shall exist
      | id | name                | todoListId | values                |
      | 3  | Cheese              | 1          | cheddar;brie;parmesan |
      | 4  | new ms prop         | 1          |                       |

  Scenario: User fails to create a multiselect property for a todo list that does not exist (Error Flow)
    When the user creates a multiselect property with name "blarg" for todo list with id "42"
    Then a list does not exist error shall be thrown
