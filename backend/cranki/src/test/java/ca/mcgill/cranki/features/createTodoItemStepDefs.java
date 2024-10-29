package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.dto.TodoItemDto;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class createTodoItemStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<?> controllerResponse;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @Given("the following todo lists exist")
    public void theFollowingTodoListsExist(DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = new TodoList();
            todoList.setName(row.get("name"));
            todoListRepository.save(todoList);
        }
    }

    @Given("no todos have been created")
    public void noTodosHaveBeenCreated() {
        todoItemRepository.deleteAll();
    }

    @Given("there exists the following todos")
    public void thereExistsTheFollowingTodos(DataTable existingItems) {
        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row: rows) {
            String name = row.get("name");
            String description = row.get("description");
            String status = row.get("status");
            String todoListName = row.get("todo list");

            TodoItem newItem = new TodoItem();
            newItem.setName(name);
            newItem.setDescription(description);
            newItem.setStatus(TodoItem.TodoStatus.valueOf(status));
            newItem.setTodoList(todoListRepository.getByName(todoListName));

            todoItemRepository.save(newItem);
        }
    }

    @When("requesting the creation of todo with name {string} and description {string} to the todo list {string}")
    public void requestingTheCreationOfTodoWithNameAndDescription(String name, String description, String todoListName) {
        TodoItemDto newItem = new TodoItemDto();
        newItem.setName(name);
        newItem.setDescription(description);

        controllerResponse = todoItemController.createTodoItem(newItem, todoListName);
    }

    @Then("the todo with name {string} and description {string} exists with status {string} in the todo list {string}.")
    public void theTodoWithNameAndDescriptionAndStateNotDoneExists(String name, String description, String status, String todoListName) {
        TodoItem item = todoItemRepository.getByName(name);
        assertEquals(name, item.getName());
        assertEquals(description, item.getDescription());
        assertEquals(TodoItem.TodoStatus.valueOf(status), item.getStatus());
        assertEquals(todoListName, item.getTodoList().getName());
    }

    @Then("the following todos exist")
    public void theFollowingTodosExist(DataTable existingItems) {
        Iterable<TodoItem> todoItems = todoItemRepository.findAll();
        List<TodoItem> actualItems = new ArrayList<>();
        todoItems.forEach(actualItems::add);

        int row_number = 0;
        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row: rows) {
            String expectedName = row.get("name");
            String expectedDescription = row.get("description");
            String expectedStatus = row.get("status");
            String todoListName = row.get("todo list");

            TodoItem actualItem = actualItems.get(row_number);

            assertEquals(expectedName, actualItem.getName());
            assertEquals(expectedDescription, actualItem.getDescription());
            assertEquals(TodoItem.TodoStatus.valueOf(expectedStatus), actualItem.getStatus());
            assertEquals(todoListName, actualItem.getTodoList().getName());

            row_number++;
        }
    }

    @Then("the following error message is returned: {string}")
    public void theFollowingErrorMessageIsReturned(String expectedMessage) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }
}
