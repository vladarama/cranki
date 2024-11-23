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

    private ResponseEntity<Object> controllerResponse;
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
            todoItem.setLiteralPropertyValue(propertyRepository.getByName(propertyValue));
            todoItemRepository.save(todoItem);
        }
    }

    @When("I select the {string} category filter")
    public void iSelectTheCategoryFilter(String selectedValue) {
        controllerResponse = todoItemController.filterTodosByProperty("Category", selectedValue);
        if (controllerResponse.getBody() instanceof List<?> responseList) {
            filteredTodos = responseList.stream()
                    .filter(TodoItemDto.class::isInstance)
                    .map(TodoItemDto.class::cast)
                    .collect(Collectors.toList());
        }
    }

    @When("no category filter is selected")
    public void noCategoryFilterIsSelected() {
        controllerResponse = todoItemController.getAllTodos();
        if (controllerResponse.getBody() instanceof List<?> responseList) {
            filteredTodos = responseList.stream()
                    .filter(TodoItemDto.class::isInstance)
                    .map(TodoItemDto.class::cast)
                    .collect(Collectors.toList());
        }
    }

    @When("the property filter list does not load")
    public void thePropertyFilterListDoesNotLoad() {
        controllerResponse = todoItemController.filterTodosByProperty("Category", ""); // Assuming invalid input
    }

    @Then("only the todos {string} and {string} should be displayed")
    public void onlyTheTodosShouldBeDisplayed(String todo1, String todo2) {
        List<String> expectedTodos = List.of(todo1, todo2);
        List<String> actualTodos = filteredTodos.stream()
                .map(TodoItemDto::getName)
                .collect(Collectors.toList());
        assertEquals(expectedTodos, actualTodos);
    }

    @Then("all todos should be displayed")
    public void allTodosShouldBeDisplayed() {
        List<TodoItem> todoItems = convertIterableToList(todoItemRepository.findAll());
        List<String> expectedTodos = todoItems.stream()
                .map(TodoItem::getName)
                .collect(Collectors.toList());
        List<String> actualTodos = filteredTodos.stream()
                .map(TodoItemDto::getName)
                .collect(Collectors.toList());
        assertEquals(expectedTodos, actualTodos);
    }

    @Then("the system should display an error message {string}")
    public void theSystemShouldDisplayAnErrorMessage(String expectedMessage) {
        assertEquals(expectedMessage, controllerResponse.getBody());
    }
}
