Feature: Delete todo item

As a User
I want to delete todo item(s)
So that I can remove unnecessary todo item(s)

Scenario: Successfully delete a todo item (Normal Flow)
	Given there exists a todo item with id 1 and name "SINGLE TODO" in my list
        When I select a todo item
        And I confirm to delete such item
        Then the item is deleted

Scenario: Successfully delete multiple todo item (Alternative Flow)
	Given there exists todo items with id <id> and name <name> in my list
	 | id | name   |
	 | 01 | first  |
	 | 02 | second |
        When I select all of those items
        And I confirm to delete such items
        Then the items are deleted

Scenario: Trying to delete an already deleted todo item (Error Flow)
	Given the todo item with id 1 and name "DELETED" is deleted
        When I select a todo item with id 1
        And I confirm to delete such item
        Then a "Todo item does not exist"
