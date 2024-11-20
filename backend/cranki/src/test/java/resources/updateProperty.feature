Feature: Create a todo 

  As a Todo List User
  I would like to update a property
  so that I can change my mind about the organization of my lists.

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
      | id | name                | todoListName |
      | 3  | Cheese              | Chores       |
    And property with name "Cheese" has values
      | value     |
      | value_one |

  Scenario: User changes the name of a literal property (Normal Flow)
    When the user changes the name of a literal property with name "My Literal Property" to "Bacon"
    Then the following literal properties shall exist
      | id | name               | todoListName       |
      | 1  | Bacon              | Chores             |
      | 2  | Corresponding Duck | Tasks              |

  Scenario: User changes the name of a multiselect property (Normal Flow)
    When the user changes the name of a multiselect property with name "Cheese" to "Bacon"
    Then the following literal properties shall exist
      | id | name  | todoListName       |
      | 3  | Bacon | Chores             |

  Scenario: User fails to change the name of a property for a property that does not exist (Error Flow)
    When the user changes the name of a literal property with name "McNugget Combo" to "Big Mac"
    Then a property does not exist error shall be thrown
