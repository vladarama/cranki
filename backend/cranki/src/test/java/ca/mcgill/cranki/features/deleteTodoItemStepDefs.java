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

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class deleteTodoItemStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<String> controllerResponse;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
    }

    @Given("the following todo items exist in my todo list")
    public void theFollowingTodoItemsExistInMyTodoList(DataTable dataTable) {
        clearDatabase();

        List<Map<String, String>> rows = dataTable.asMaps();
        for (var row : rows) {
            TodoItem item = new TodoItem();
            item.setName(row.get("name"));
            todoItemRepository.save(item);
        }
    }

    @When("I delete {string}")
    public void iDelete(String todoName) {
        TodoItem item = todoItemRepository.getByName(todoName);
        controllerResponse = todoItemController.deleteTodoItem(item.getId());
    }

    @Then("{string} is deleted")
    public void isDeleted(String todoName) {
        TodoItem item = todoItemRepository.getByName(todoName);
        assertEquals(200, controllerResponse.getStatusCode().value());
        assertEquals("Todo item deleted successfully", controllerResponse.getBody());
        assertEquals(null, item); // Confirm the item no longer exists
    }

    @When("I try to delete a non-existent todo item with id {string}")
    public void iTryToDeleteANonExistentTodoItemWithId(String id) {
        controllerResponse = todoItemController.deleteTodoItem(Integer.parseInt(id));
    }

    @Then("I should receive a todo not found error")
    public void iShouldReceiveATodoNotFoundError() {
        assertEquals(404, controllerResponse.getStatusCode().value());
        assertEquals("Todo is not found", controllerResponse.getBody());
    }
}