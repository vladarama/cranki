package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

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
    }

    @Given("no todos have been created")
    public void noTodosHaveBeenCreated() {
        clearDatabase();
    }

    @Given("there exists todos with id {int} and name {string}")
    public void thereExistsTodosWithIdAndName(Integer id, String name) {
        TodoItem existingItem = new TodoItem();
        existingItem.setId(id);
        existingItem.setName(name);
        existingItem.setStatus(TodoItem.TodoStatus.NOT_DONE);
        todoItemRepository.save(existingItem);
    }

    @When("requesting the creation of todo with name {string} and description {string} to the todo list {string}")
    public void requestingTheCreationOfTodoWithNameAndDescription(String name, String description, String todoListName) {
        TodoItemDto newItem = new TodoItemDto();
        newItem.setName(name);
        newItem.setDescription(description);
        TodoList todoList = new TodoList(todoListName, new ArrayList<>());

        todoListRepository.save(todoList);
        try {
            controllerResponse = todoItemController.createTodoItem(newItem, todoListName);
        } catch (Exception e) {
            controllerResponse = ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Then("the todo with name {string} and description {string} exist with status {string} in the todo list {string}.")
    public void theTodoWithNameAndDescriptionAndStateNotDoneExists(String name, String description, String status, String todoListName) {
        TodoItem item = todoItemRepository.getByName(name);
        assertEquals(name, item.getName());
        assertEquals(description, item.getDescription());
        assertEquals(TodoItem.TodoStatus.valueOf(status), item.getStatus());
        assertEquals(todoListName, item.getTodoList().getName());
    }

    @Then("the following error message is returned: {string}")
    public void theFollowingErrorMessageIsReturned(String expectedMessage) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }
}
