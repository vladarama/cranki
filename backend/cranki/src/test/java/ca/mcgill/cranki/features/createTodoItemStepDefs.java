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

    private ResponseEntity<String> controllerResponse;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @Given("no todos have been created")
    public void noTodosHaveBeenCreated() {
        clearDatabase();
    }

    @Given("there exists the following todos in the todo list named {string}")
    public void thereExistsTheFollowingTodos(DataTable existingItems, String todoListName) {
        clearDatabase();

        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row: rows) {
            String id = row.get("id");
            String name = row.get("name");
            String description = row.get("description");
            String status = row.get("status");

            TodoItem newItem = new TodoItem();
            newItem.setId(Integer.parseInt(id));
            newItem.setName(name);
            newItem.setDescription(description);
            newItem.setStatus(TodoItem.TodoStatus.valueOf(status));

            newItem.setTodoList(todoListRepository.getByName(todoListName));

            todoItemRepository.save(newItem);
        }

        Iterable<TodoItem> todoItemsIterable = todoItemRepository.findAll();
        List<TodoItem> todoItems = new ArrayList<>();
        todoItemsIterable.forEach(todoItems::add);

        TodoList todoList = todoListRepository.getByName(todoListName);
        if (todoList == null) {
            todoList = new TodoList(todoListName, todoItems);
        } else {
            todoList.setItems(todoItems);
        }
        todoListRepository.save(todoList);
    }

    @When("requesting the creation of todo with name {string} and description {string} to the todo list {string}")
    public void requestingTheCreationOfTodoWithNameAndDescription(String name, String description, String todoListName) {
        TodoItemDto newItem = new TodoItemDto();
        newItem.setName(name);
        newItem.setDescription(description);

        if (todoListRepository.getByName(todoListName) == null) {
            TodoList todoList = new TodoList(todoListName, new ArrayList<>());
            todoListRepository.save(todoList);
        }

        controllerResponse = todoItemController.createTodoItem(newItem, todoListName);
    }

    @Then("the todo with name {string} and description {string} exist with status {string} in the todo list {string}.")
    public void theTodoWithNameAndDescriptionAndStateNotDoneExists(String name, String description, String status, String todoListName) {
        TodoItem item = todoItemRepository.getByName(name);
        assertEquals(name, item.getName());
        assertEquals(description, item.getDescription());
        assertEquals(TodoItem.TodoStatus.valueOf(status), item.getStatus());
        assertEquals(todoListName, item.getTodoList().getName());
    }

    @Then("the following todos exist in the todo list named {string}")
    public void theFollowingTodosExist(DataTable existingItems, String todoListName) {
        Iterable<TodoItem> todoItems = todoItemRepository.findAll();
        List<TodoItem> actualItems = new ArrayList<>();
        todoItems.forEach(actualItems::add);

        int row_number = 0;
        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row: rows) {
            String expectedId = row.get("id");
            String expectedName = row.get("name");
            String expectedDescription = row.get("description");
            String expectedStatus = row.get("status");

            TodoItem actualItem = actualItems.get(row_number);

            assertEquals(Integer.parseInt(expectedId), actualItem.getId());
            assertEquals(expectedName, actualItem.getName());
            assertEquals(expectedDescription, actualItem.getDescription());
            assertEquals(TodoItem.TodoStatus.valueOf(expectedStatus), actualItem.getStatus());

            row_number++;
        }
    }

    @Then("the following error message is returned: {string}")
    public void theFollowingErrorMessageIsReturned(String expectedMessage) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }
}
