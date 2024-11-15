package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoListController;
import ca.mcgill.cranki.dto.AddTodoListDto;
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
import static org.junit.Assert.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class createTodoListStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoListController todoListController;

    private ResponseEntity<Object> controllerResponse;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @Given("Given the following to-do lists exist")
    public void theFollowingTodoListsExist(DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = new TodoList();
            todoList.setName(row.get("name"));
            todoListRepository.save(todoList);
        }
    }

    @When("the user creates a new to-do list with the name {string}")
    public void theUserCreatesANewTodoListWithTheName(String name) {
        AddTodoListDto newItem = new AddTodoListDto();
        newItem.setName(name);

        controllerResponse = todoListController.createTodoList(newItem);
    }

    @Then("the new to-do list {string} is created with the Id {int}")
    public void theNewTodoListIsCreatedWithTheId(String name, int Id) {
        TodoList item = todoListRepository.getByName(name);
        assertEquals(name, item.getName());
        assertEquals(Id, item.getId());
    }

    @Given("a to-do list {string} already exists")
    public void aTodoListAlreadyExists(String name) {
        TodoList existingList = new TodoList();
        existingList.setName(name);
        todoListRepository.save(existingList);
    }

    @When("the user attempts to create a new to-do list with the name {string}")
    public void theUserAttemptsToCreateANewTodoListWithTheName(String name) {
        AddTodoListDto newItem = new AddTodoListDto();
        newItem.setName(name);

        controllerResponse = todoListController.createTodoList(newItem);
    }

    @Then("an error message {string} is displayed")
    public void theFollowingErrorMessageIsReturned(String expectedMessage) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }

    @When("the user attempts to create a to-do list without providing a name")
    public void theUserAttemptsToCreateATodoListWithoutProvidingAName() {
        AddTodoListDto newItem = new AddTodoListDto();
        newItem.setName("");

        controllerResponse = todoListController.createTodoList(newItem);
    }

    @When("the user updates the name of the to-do list with Id {int} to {string}")
    public void theUserUpdatesTheNameOfTheTodoListWithIdTo(int id, String newName) {
        todoListController.editTodoListName(id, newName);
    }

    @Then("the to-do list with Id {int} is updated to {string}")
    public void theTodoListWithIdIsUpdatedTo(int id, String newName) {
        TodoList updatedList = todoListRepository.findById(id).orElseThrow();
        assertEquals(newName, updatedList.getName());
    }

    @When("the user cancels the update")
    public void theUserCancelsTheUpdate() {
        // Not sure what to do with this? Shouldn't trigger any changes right?
    }

    @Then("the to-do list retains its original name {string}")
    public void theTodoListRetainsItsOriginalName(String originalName) {
        TodoList list = todoListRepository.getByName(originalName);
        assertEquals(originalName, list.getName());
    }

    @When("the user deletes the to-do list with Id {int}")
    public void theUserDeletesTheTodoListWithId(int id) {
        todoListController.deleteTodoList(id);
    }

    @Then("the to-do list with Id {int} is removed")
    public void theTodoListWithIdIsRemoved(int id) {
        assertFalse(todoListRepository.existsById(id));
    }

    @Then("the to-do list with Id {int} is no longer available on the dashboard")
    public void theTodoListWithIdIsNoLongerAvailableOnTheDashboard(int id) {
        assertFalse(todoListRepository.existsById(id));
    }

    @When("the user attempts to delete a non-existing to-do list with Id {int}")
    public void theUserAttemptsToDeleteANonExistingTodoListWithId(int id) {
        ResponseEntity<String> stringResponse = todoListController.deleteTodoList(id);
    
        controllerResponse = ResponseEntity
            .status(stringResponse.getStatusCode())
            .headers(stringResponse.getHeaders())
            .body((Object) stringResponse.getBody());
    }

    @Then("an error message {string} is displayed")
    public void anErrorMessageIsDisplayed(String expectedMessage) {
        assertEquals(404, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }
}

