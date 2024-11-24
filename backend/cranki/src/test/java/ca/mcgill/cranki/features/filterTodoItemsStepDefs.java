package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.Property;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.PropertyRepository;
import ca.mcgill.cranki.repository.TodoItemRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class filterTodoItemsStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<List<TodoItemDto>> controllerResponse;

    private List<TodoItemDto> filteredTodos;

    // Helper method to convert Iterable to List
    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    @Given("I have the following todos with property {string}:")
    public void iHaveTheFollowingTodosWithProperty(String propertyName, DataTable dataTable) {
        var rows = dataTable.asMaps();
    for (var row : rows) {
        var todoName = row.get("Name");
        var propertyValue = row.get(propertyName);

        TodoItem todoItem = new TodoItem();
        todoItem.setName(todoName);
        todoItem.setLiteralPropertyValue(propertyValue); // Ensure this is set correctly
        todoItemRepository.save(todoItem);
    }
    }

    @Given("I have the following todos with no property added:")
    public void i_have_the_following_todos_with_no_property_added(io.cucumber.datatable.DataTable dataTable) {

    }



    //MP
    @When("I select the {string} category filter")
    public void iSelectTheCategoryFilter(String selectedValue) {
        controllerResponse = todoItemController.filterTodosByProperty("Category", selectedValue);
    }

    //MP
    @When("no category filter is selected")
    public void noCategoryFilterIsSelected() {
        controllerResponse = todoItemController.filterTodosByProperty("Category", ""); // Pass empty value
    }

    //MP
    @When("the property filter list does not load")
        public void thePropertyFilterListDoesNotLoad() {
        controllerResponse = todoItemController.filterTodosByProperty("Category", null); // Pass empty value
    }

    @Then("only the todos {string} and {string} should be displayed")
    public void onlyTheTodosShouldBeDisplayed(String todo1, String todo2) {
      
        List<TodoItemDto> todosFromResponse = controllerResponse.getBody();
        List<String> actualTodos = todosFromResponse.stream()
                .map(TodoItemDto::getName)
                .collect(Collectors.toList());

        List<String> expectedTodos = List.of(todo1, todo2);
        assertEquals(expectedTodos, actualTodos);
    }


    @Then("all todos should be displayed")
    public void allTodosShouldBeDisplayed() {
        List<TodoItemDto> todosFromResponse = controllerResponse.getBody();
        List<String> actualTodos = convert(todosFromResponse);
               

        List<TodoItem> todoItems = convertIterableToList(todoItemRepository.findAll());
        List<String> expectedTodos = convert2(todoItems);

        assertEquals(expectedTodos, actualTodos);
    }


    @Then("the system should display an error message {string}")
    public void theSystemShouldDisplayAnErrorMessage(String expectedMessage) {
        assertEquals(expectedMessage, controllerResponse.getBody());
    }

    public List<String> convert(List<TodoItemDto> filteredTodos) {
        // Create an empty list to store the converted TodoItemDto objects
        List<String> todoDtos = new ArrayList<>();
    
        // Iterate through each TodoItem in the input list
        for (TodoItemDto item : filteredTodos) {
            // Convert each TodoItem to a TodoItemDto
            // Add the converted TodoItemDto to the output list
            todoDtos.add(item.getName());
        }
    
        // Return the list of TodoItemDto objects
        return todoDtos;
    }
    public List<String> convert2(List<TodoItem> filteredTodos) {
        // Create an empty list to store the converted TodoItemDto objects
        List<String> todoDtos = new ArrayList<>();
    
        // Iterate through each TodoItem in the input list
        for (TodoItem item : filteredTodos) {
            // Convert each TodoItem to a TodoItemDto
            // Add the converted TodoItemDto to the output list
            todoDtos.add(item.getName());
        }
    
        // Return the list of TodoItemDto objects
        return todoDtos;
    }

}
