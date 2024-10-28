Feature: Delete todo item

As a User
I want to delete todo item(s)
So that I can remove unnecessary todo item(s)

Background:
        Given the following todo items exist in my todo list
                | id | name   |
	        | 1  | first  |
	        | 2  | second |

Scenario: Successfully delete a todo item (Normal Flow)
        When I delete "first"
        Then "first" is deleted

Scenario: Successfully delete multiple todo items (Alternate Flow)
        When I delete "first"
        And I delete "second"
        Then "first" is deleted
        And "second" is deleted

Scenario: Trying to delete a non-existent todo item (Error Flow)
        When I try to delete a non-existent todo item with id "999"
        Then I should receive a todo not found error
