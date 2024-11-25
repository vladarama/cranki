Feature: Add Todo Properties

As a user, I would like to add properties to my todos so that I can add details to my task list.

Scenario: User creates a single-select property
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	When the User selects the todo with "Buy groceries"
	And the User adds the single-select property "assignment" with the values "you", "me", and "dad"
	And the User selects "you" for the property "assignment"
	Then the todo "Buy groceries" should be displayed with the value "you" for the property "assignment"

Scenario: User creates a single-select property without choosing a value (alternative flow)
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	When the User selects the todo with "Buy groceries"
	And the User adds the single-select property "assignment" with the values "you", "me", and "dad"
	Then the todo "Buy groceries" should be displayed with the property "assignment" with no value

Scenario: User sets a single-select property
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	And the single-select property "assignment" exists with the values "you", "me", and "dad"
	When the User selects the todo with "Buy groceries"
	And the User selects "you" for the property "assignment"
	Then the todo "Buy groceries" should be displayed with the value "you" for the property "assignment"

Scenario: User sets a single-select property to multiple values (Error flow)
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	And the single-select property "assignment" exists with the values "you", "me", and "dad"
	When the User selects the todo with "Buy groceries"
	And the User selects "you" and "me" for the property "assignment"
	Then the system should display an error indicating that single-select properties only allow one value

Scenario: User creates a multi-select property
	Given the User is on the todo list page
	And there is a todo "Buy groceries"
	And the multi-select property "assignment" exists with the values "you", "me", and "dad"
	When the User selects the todo with "Buy groceries"
	And the User selects "you" and "me" for the property "assignment"
	Then the todo "Buy groceries" should be displayed with the value "you" and "me" for the property "assignment"

Scenario: User sets a literal property
	Given I am on the todo list page
	And there is a todo "Buy groceries"
	And the literal property "note" exists
	When I add a new todo with the text "Buy groceries"
	And I add the property value "must do this afternoon" for property "note"
	Then the todo "Buy groceries" should be displayed with the value "must do this afternoon" for the property "note"

Scenario: User edits a literal property
	Given I am on the todo list page
	And there is a todo "Buy groceries" with the property "note" with value "must do this afternoon"
	When I select the todo with "Buy groceries"
	And I edit the property value to "must do tomorrow" for property "note"
	Then the todo "Buy groceries" should be displayed with the value "must do tomorrow" for the property "note"
