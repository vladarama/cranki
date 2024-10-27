package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class editTodoNameStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<String> controllerResponse;
    private TodoItem testTodoItem;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
    }

    @Given("the following todo exists")
    public void theFollowingTodoExists(io.cucumber.datatable.DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            testTodoItem = new TodoItem();
            testTodoItem.setName(row.get("name"));
            testTodoItem = todoItemRepository.save(testTodoItem);
        }
    }

    @When("I edit the todo item name to {string}")
    public void iEditTheTodoItemNameTo(String newName) {
        controllerResponse = todoItemController.editTodoName(testTodoItem.getId(), newName);
    }

    @Then("the todo item name should be {string}")
    public void theTodoItemNameShouldBe(String expectedName) {
        assertEquals(200, controllerResponse.getStatusCode().value());
        assertEquals("Todo item name updated successfully", controllerResponse.getBody());

        TodoItem updated = todoItemRepository.findById(testTodoItem.getId()).get();
        assertEquals(expectedName, updated.getName());
    }

    @When("I try to edit a non-existent todo item with id {string}")
    public void iTryToEditANonExistentTodoItem(String id) {
        controllerResponse = todoItemController.editTodoName(Integer.parseInt(id), "New Name");
    }
    
    @Then("I should receive a not found error")
    public void iShouldReceiveANotFoundError() {
        assertEquals(404, controllerResponse.getStatusCode().value());
        assertEquals("Todo is not found", controllerResponse.getBody());
    }

    @When("I try to edit the todo item name to empty")
    public void iTryToEditTheTodoItemNameToEmpty() {
        controllerResponse = todoItemController.editTodoName(testTodoItem.getId(), "   ");
    }

    @Then("I should receive an empty name error")
    public void iShouldReceiveAnEmptyNameError() {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals("Name cannot be empty", controllerResponse.getBody());
    }
}
