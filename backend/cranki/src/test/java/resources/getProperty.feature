Feature: Add Todo Properties

As a user, I would like to add properties to my todos so that I can add details to my task list.

Scenario: User creates a single-select property
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	When the User selects the todo with "Buy groceries"
	And the User adds the single-select property "assignment" with the values "you", "me", and "dad"
	And the User selects "you" for the property "assignement"
	Then the todo "Buy groceries" should be displayed with the value "you" for the property "assignement"

Scenario: User creates a single-select property without choosing a value (alternative flow)
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	When the User selects the todo with "Buy groceries"
	And the User adds the single-select property "assignment" with the values "you", "me", and "dad"
	Then the todo "Buy groceries" should be displayed with the property "assignement" with no value

Scenario: User sets a single-select property
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	And the single-select property "assignment" exists with the values "you", "me", and "dad"
	When the User selects the todo with "Buy groceries"
	And the User selects "you" for the property "assignement"
	Then the todo "Buy groceries" should be displayed with the value "you" for the property "assignement"

Scenario: User sets a single-select property to multiple values (Error flow)
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	And the single-select property "assignment" exists with the values "you", "me", and "dad"
	When the User selects the todo with "Buy groceries"
	And the User selects "you" and "me" for the property "assignement"
	Then the system should display an error indicating that single-select properties only allow one value

Scenario: User creates a multi-select property
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	And the multi-select property "assignment" exists with the values "you", "me", and "dad"
	When the User selects the todo with "Buy groceries"
	And the User selects "you" and "me" for the property "assignement"
	Then the todo "Buy groceries" should be displayed with the value "you" and "me" for the property "assignement"

Scenario: User sets a literal property
	Given I am on the todo list page
	When I add a new todo with the text "Buy groceries"
	And I add the property "must do this afternoon"
	And I click the "Add Todo" button
	Then the todo "Buy groceries" should be displayed with the property "must do this afternoon"

Scenario: User edits a literal property
	Given I am on the todo list page
	And there is a todo "Buy groceries" with the property "must do this afternoon"
	When I select the todo with "Buy groceries"
	And I edit the property "must do this afternoon" to "must do tomorrow"
	Then the todo "Buy groceries" should be displayed with the property "must do tomorrow"
