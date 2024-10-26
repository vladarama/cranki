Feature: Mark todo

  As a user
  I would like to mark a todo in a todo list
  So that I am aware of the status of my tasks

  Scenario: Mark a todo as DONE (Normal Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | IN_PROGRESS |
      | ECSE428 hw    | IN_PROGRESS |
    When I mark "Buy groceries" as DONE
    Then the status of "Buy groceries" should be "DONE"
    And the status of "ECSE428 hw" should be "IN_PROGRESS"

  Scenario: Mark multiple todos as DONE (Alternate Flow)
    Given I have the following todos in my todo list:
      | Todo Name       | Status      |
      | Buy groceries   | IN_PROGRESS |
      | ECSE428 hw      | IN_PROGRESS |
      | Check MyCourses | IN_PROGRESS |
    When I mark "Buy groceries" as DONE
    And I mark "ECSE428 hw" as DONE
    Then the status of "Buy groceries" should be "DONE"
    And the status of "ECSE428 hw" should be "DONE"
    And the status of "Check MyCourses" should be "IN_PROGRESS"

  Scenario: Mark a todo as IN_PROGRESS (Alternate Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | DONE        |
      | ECSE428 hw    | DONE        |
    When I mark "Buy groceries" as IN_PROGRESS
    Then the status of "Buy groceries" should be "IN_PROGRESS"
    And the status of "ECSE428 hw" should be "DONE"

  Scenario: Mark multiple todos as IN_PROGRESS (Alternate Flow)
    Given I have the following todos in my todo list:
      | Todo Name       | Status      |
      | Buy groceries   | DONE        |
      | ECSE428 hw      | DONE        |
      | Check MyCourses | DONE        |
    When I mark "Buy groceries" as IN_PROGRESS
    And I mark "ECSE428 hw" as IN_PROGRESS
    Then the status of "Buy groceries" should be "IN_PROGRESS"
    And the status of "ECSE428 hw" should be "IN_PROGRESS"
    And the status of "Check MyCourses" should be "DONE"


  Scenario: Mark a todo as DONE that is already marked as DONE (Error Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | DONE        |
      | ECSE428 hw    | IN_PROGRESS |
    When I mark "Buy groceries" as DONE
    Then the status of "Buy groceries" should remain "DONE"
    And the status of "ECSE428 hw" should be "IN_PROGRESS"
    And I should see an error message "Task is already marked as DONE"

  Scenario: Mark a todo as IN_PROGRESS that is already marked as IN_PROGRESS (Error Flow)
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | DONE        |
      | ECSE428 hw    | IN_PROGRESS |
    When I mark "ECSE428 hw" as IN_PROGRESS
    Then the status of "ECSE428 hw" should remain "IN_PROGRESS"
    And the status of "Buy groceries" should be "DONE"
    And I should see an error message "Task is already marked as IN_PROGRESS"

  Scenario: Attempt to mark a todo as done that does not exist
    Given I have the following todos in my todo list:
      | Todo Name     | Status      |
      | Buy groceries | DONE        |
      | ECSE428 hw    | IN_PROGRESS |
    When I mark "Go to class" as DONE
    Then I should see an error message "Task not found"
