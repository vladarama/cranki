package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class prioritizeTodoItemsStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<String> controllerResponse;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @Given("the following todos exist in the {string} todo list:")
    public void theFollowingTodosExistInTheTodoList(String listName, DataTable todos) {
        clearDatabase();

        // Create and save the TodoList
        TodoList todoList = new TodoList();
        todoList.setName(listName);
        todoListRepository.save(todoList);

        // Add todos to the database
        List<Map<String, String>> rows = todos.asMaps();
        for (var row : rows) {
            TodoItem newItem = new TodoItem();
            newItem.setName(row.get("Name"));
            newItem.setPriority(parsePriority(row.get("Priority")));
            newItem.setTodoList(todoList);
            todoItemRepository.save(newItem);
        }
    }

    @When("the user creates a new todo with the following details:")
    public void theUserCreatesANewTodoWithTheFollowingDetails(DataTable newTodoData) {
        Map<String, String> row = newTodoData.asMaps().get(0);

        // Retrieve the TodoList by name
        String listName = row.get("list_id");
        TodoList todoList = todoListRepository.getByName(listName);

        if (todoList == null) {
            throw new RuntimeException("Todo list not found");
        }

        // Create and save the new TodoItem
        TodoItem newItem = new TodoItem();
        newItem.setName(row.get("Name"));
        newItem.setPriority(parsePriority(row.get("Priority")));
        newItem.setTodoList(todoList);
        todoItemRepository.save(newItem);
    }

    @When("the user requests to set the task with id {int} to {string} priority")
    public void theUserRequestsToSetTheTaskWithIdToPriority(int id, String priority) {
        controllerResponse = todoItemController.updateTodoPriority(id, priority.toUpperCase());
    }

    @Then("the new todo is added to the list with the following order and priorities:")
    @Then("the todo list is updated with the following order and priorities:")
    public void theTodoListMatchesExpectedOrder(DataTable expectedOrder) {
        // Convert Iterable to List
        List<TodoItem> items = new ArrayList<>();
        todoItemRepository.findAll().forEach(items::add);

        // Sort items
        items.sort(Comparator.comparing((TodoItem item) -> item.getPriority().ordinal())
                .reversed()
                .thenComparing(TodoItem::getId));

        // Compare sorted items to expected order
        List<Map<String, String>> rows = expectedOrder.asMaps();
        for (int i = 0; i < rows.size(); i++) {
            TodoItem actualItem = items.get(i);
            Map<String, String> expectedRow = rows.get(i);

            assertEquals(expectedRow.get("Name"), actualItem.getName());
            assertEquals(expectedRow.get("Priority").toUpperCase(), actualItem.getPriority().name());
        }
    }

    @Then("the error message {string} is returned")
    public void theErrorMessageIsReturned(String expectedMessage) {
        // Verify that the error response matches the expected message
        assertNotNull(controllerResponse);
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }

    private TodoItem.TodoPriority parsePriority(String priority) {
        // Helper to parse priority or return LOW if null/empty
        return (priority == null || priority.isEmpty()) ? TodoItem.TodoPriority.LOW
                : TodoItem.TodoPriority.valueOf(priority.toUpperCase());
    }
}