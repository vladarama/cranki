package ca.mcgill.cranki.features;

import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class markTodoStatusStepDefs {
    @Autowired
    private TodoItemRepository todoItemRepository;

    private void clearDatabase(){
        todoItemRepository.deleteAll();
    }

    @Given("I have the following todos in my todo list:")
    public void iHaveTheFollowingTodosInMyTodoList(DataTable existingItems) {
        clearDatabase();

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

        String url = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8080")
                .path("/todoItem/updateStatus")
                .queryParam("id", 1)
                .queryParam("status", "DONE")
                .encode()
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String body = "";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        System.out.println("BEFORE BFORE BFORE: " + todoItemRepository.getByName(arg0).getStatus());
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        HttpStatusCode httpStatus = responseEntity.getStatusCode();
        int status = httpStatus.value();
        String response = responseEntity.getBody();
        System.out.println("AFTER AFTER AFTER: " + todoItemRepository.getByName(arg0).getStatus() + " " + status + response);
    }

    @Then("the status of {string} should be {string}")
    public void theStatusOfShouldBe(String arg0, String arg1) {
        TodoItem item = todoItemRepository.getByName(arg0);
        assertEquals(TodoItem.TodoStatus.valueOf(arg1), item.getStatus());
    }

    @And("the tatus of {string} should be {string}")
    public void theTatusOfShouldBe(String arg0, String arg1) {
    }

    @When("I mark {string} as IN_PROGRESS")
    public void iMarkAsInProgress(String arg0) {
    }

    @Then("the status of {string} should remain {string}")
    public void theStatusOfShouldRemain(String arg0, String arg1) {
    }

    @And("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String arg0) {
    }
}
