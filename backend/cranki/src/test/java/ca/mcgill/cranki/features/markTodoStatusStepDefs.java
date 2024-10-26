package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class markTodoStatusStepDefs {
    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<String> controllerResponse;
    private String error;

    private void clearDatabase(){
        todoItemRepository.deleteAll();
    }

    @Given("I have the following todos in my todo list:")
    public void iHaveTheFollowingTodosInMyTodoList(DataTable existingItems) {
        clearDatabase();

        error = "";

        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row: rows) {
            String name = row.get("Todo Name");
            String status = row.get("Status");

            TodoItem newItem = new TodoItem();
            newItem.setName(name);
            newItem.setStatus(TodoItem.TodoStatus.valueOf(status));

            todoItemRepository.save(newItem);
        }
        System.out.println("SIZE SIZE SIZE: " + todoItemRepository.count());
    }

    @When("I mark {string} as DONE")
    public void iMarkAsDone(String arg0) {
        TodoItem item = todoItemRepository.getByName(arg0);
        int id;
        if (item != null){
            id = item.getId();
        } else {
            id = 99;
        }
        controllerResponse = todoItemController.updateTodoStatus(id, "DONE");
    }

    @Then("the status of {string} should be {string}")
    public void theStatusOfShouldBe(String arg0, String arg1) {
        TodoItem item = todoItemRepository.getByName(arg0);
        assertEquals(TodoItem.TodoStatus.valueOf(arg1), item.getStatus());
    }

    @When("I mark {string} as IN_PROGRESS")
    public void iMarkAsInProgress(String arg0) {
        TodoItem item = todoItemRepository.getByName(arg0);
        int id;
        if (item != null){
           id = item.getId();
        } else {
            id = 99;
        }
        controllerResponse = todoItemController.updateTodoStatus(id, "IN_PROGRESS");
    }

    @Then("the status of {string} should remain {string}")
    public void theStatusOfShouldRemain(String arg0, String arg1) {
        TodoItem item = todoItemRepository.getByName(arg0);
        assertEquals(TodoItem.TodoStatus.valueOf(arg1), item.getStatus());
    }

    @And("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String arg0) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(arg0, controllerResponse.getBody());
    }


}
