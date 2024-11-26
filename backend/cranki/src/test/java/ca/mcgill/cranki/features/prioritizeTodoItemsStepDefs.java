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

    @Given("the following todo list exists for the user:")
    public void theFollowingTodoListExistsForTheUser(DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = new TodoList();
            todoList.setName(row.get("name"));
            todoListRepository.save(todoList);
        }
    }

    @Given("the following todos exist:")
    public void theFollowingTodosExist(DataTable existingItems) {
        todoItemRepository.deleteAll();
        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row : rows) {
            String name = row.get("name");
            TodoItem.TodoPriority priority = parsePriority(row.get("priority"));
            String todoListName = row.get("todo list");

            TodoItem newItem = new TodoItem();
            newItem.setName(name);
            newItem.setPriority(priority);
            newItem.setTodoList(todoListRepository.getByName(todoListName));

            todoItemRepository.save(newItem);
        }
    }

    @When("the user creates a new todo with the following details:")
    public void theUserCreatesANewTodoWithTheFollowingDetails(DataTable newTodoData) {
        Map<String, String> row = newTodoData.asMaps().get(0);

        String name = row.get("name");
        TodoItem.TodoPriority priority = parsePriority(row.get("priority"));
        String todoListName = row.get("todo list");

        TodoItem newItem = new TodoItem();
        newItem.setName(name);
        newItem.setPriority(priority);
        newItem.setTodoList(todoListRepository.getByName(todoListName));

        todoItemRepository.save(newItem);
    }

    @When("the user requests to set the task with name {string} to {string} priority:")
    public void theUserRequestsToSetTheTaskWithIdToPriority(String name, String priority) {
        TodoItem item = todoItemRepository.getByName(name);
        if (item == null) {
            controllerResponse = ResponseEntity.badRequest().body("Task not found");
            return;
        }
        controllerResponse = todoItemController.updateTodoPriority(item.getId(), priority);
    }

    @Then("the new todo is added to the list with the following order and priorities:")
    @Then("the todo list is updated with the following order and priorities:")
    public void theTodoListMatchesExpectedOrder(DataTable expectedOrder) {
        List<TodoItem> items = new ArrayList<>();
        todoItemRepository.findAll().forEach(items::add);

        items.sort(Comparator.comparing((TodoItem item) -> item.getPriority().ordinal())
                .reversed()
                .thenComparing(TodoItem::getId));

        List<Map<String, String>> rows = expectedOrder.asMaps();
        assertEquals(rows.size(), items.size()); // Validate list sizes match

        for (int i = 0; i < rows.size(); i++) {
            TodoItem actualItem = items.get(i);
            Map<String, String> expectedRow = rows.get(i);

            assertEquals(expectedRow.get("name"), actualItem.getName());
            assertEquals(expectedRow.get("priority"), actualItem.getPriority().name());
            assertEquals(expectedRow.get("todo list"), actualItem.getTodoList().getName());
        }
    }

    @Then("the error message {string} is returned")
    public void theErrorMessageIsReturned(String expectedMessage) {
        // Verify that the error response matches the expected message
        assertNotNull(controllerResponse); // Ensure the response is not null
        assertEquals(400, controllerResponse.getStatusCode().value()); // Check HTTP status code
        assertEquals(expectedMessage, controllerResponse.getBody()); // Check the message
    }

    private TodoItem.TodoPriority parsePriority(String priority) {
        // Helper to parse priority or return LOW if null/empty
        return (priority == null || priority.isEmpty()) ? TodoItem.TodoPriority.LOW
                : TodoItem.TodoPriority.valueOf(priority);
    }

}