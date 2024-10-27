package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class createTodoItemStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

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
        existingItem.setStatus(TodoItem.TodoStatus.INCOMPLETE);
        todoItemRepository.save(existingItem);
    }

    @When("requesting the creation of todo {int} with name {string}")
    public void requestingTheCreationOfTodoWithIdAndName(Integer id, String name) {
        TodoItem newItem = new TodoItem();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setStatus(TodoItem.TodoStatus.INCOMPLETE);
        try {
            controllerResponse = todoItemController.createTodoItem(newItem);
        } catch (Exception e) {
            controllerResponse = ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Then("the todo {int} with name {string} and state Incomplete exists")
    public void theTodoWithIdAndNameAndStateIncompleteExists(Integer id, String name) {
        TodoItem item = todoItemRepository.findById(id).orElse(null);
        assertEquals(name, item.getName());
        assertEquals(TodoItem.TodoStatus.INCOMPLETE, item.getStatus()); //incomplete not a field
    }

    @Then("the following error message is returned: {string}")
    public void theFollowingErrorMessageIsReturned(String expectedMessage) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }
}
