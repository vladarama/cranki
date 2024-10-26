Feature: Mark todo

  As a user
  I would like to mark a todo in a todo list
  So that I am aware of the status of my tasks

  Scenario: Mark a todo as done (Normal Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | in progress |
      | ECSE428 hw    | in progress |
    When I mark "Buy groceries" as done
    Then the status of "Buy groceries" should be "done"
    And the tatus of "ECSE428 hw" should be "in progress"

  Scenario: Mark multiple todos as done (Alternate Flow)
    Given I have the following todos in my todo list:
      | Todo Name       | Status      |
      | Buy groceries   | in progress |
      | ECSE428 hw      | in progress |
      | Check MyCourses | in progress |
    When I mark "Buy groceries" as done
    And I mark "ECSE428 hw" as done
    Then the status of "Buy groceries" should be "done"
    And the status of "ECSE428 hw" should be "done"
    And the status of "Check MyCourses" should be "in progress"

  Scenario: Mark a todo as in progress (Alternate Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | done        |
      | ECSE428 hw    | done        |
    When I mark "Buy groceries" as in progress
    Then the status of "Buy groceries" should be "in progress"
    And the status of "ECSE428 hw" should be "done"

  Scenario: Mark multiple todos as in progress (Alternate Flow)
    Given I have the following todos in my todo list:
      | Todo Name       | Status      |
      | Buy groceries   | done        |
      | ECSE428 hw      | done        |
      | Check MyCourses | done        |
    When I mark "Buy groceries" as in progress
    And I mark "ECSE428 hw" as in progress
    Then the status of "Buy groceries" should be "in progress"
    And the status of "ECSE428 hw" should be "in progress"
    And the status of "Check MyCourses" should be "done"


  Scenario: Mark a todo as done that is already marked as done (Error Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | done        |
      | ECSE428 hw    | in progress |
    When I mark "Buy groceries" as done
    Then the status of "Buy grocieres" should remain "done"
    And the status of "ECSE428 hw" should be "in progress"
    And I should see an error message "Task is already marked as done"

  Scenario: Mark a todo as in progress that is already marked as in progress (Error Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | done        |
      | ECSE428 hw    | in progress |
    When I mark "ECSE428 hw" as in progress
    Then the status of "ECSE428 hw" should remain "in progress"
    And the status of "Buy groceries" should be "done"
    And I should see an error message "Task is already marked as in progress"

  Scenario: Attempt to mark a todo as done that does not exist
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | done        |
      | ECSE428 hw    | in progress |
    When I mark "Go to class" as done
    Then I should see an error message "Task not found"