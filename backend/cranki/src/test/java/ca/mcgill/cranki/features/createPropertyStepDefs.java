package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.controller.properties.PropertyController;
import ca.mcgill.cranki.dto.LiteralPropertyDto;
import ca.mcgill.cranki.dto.MultiselectPropertyDto;
import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.*;
import ca.mcgill.cranki.repository.PropertyRepository;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class createPropertyStepDefs {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyController propertyController;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<Object> controllerResponse;

    private void clearDatabase() {
        propertyRepository.deleteAll();
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @Given("the following todo lists exist")
    public void theFollowingTodoListsExist(DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = new TodoList();
            todoList.setName(row.get("name"));
            todoListRepository.save(todoList);
        }
    }

    @And("the following literal properties exist")
    public void theFollowingLiteralPropertiesExist(DataTable dataTable) {
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = todoListRepository.findById(Integer.parseInt(row.get("todoListId"))).get();
            List<Property> existingProperties = todoList.getProperty();

            LiteralProperty literalProperty = new LiteralProperty();
            literalProperty.setName(row.get("name"));
            literalProperty.setTodoList(todoList);
            existingProperties.add(literalProperty);
            propertyRepository.save(literalProperty);

            todoList.setProperty(existingProperties);
            todoListRepository.save(todoList);
        }

    }

    @And("the following multiselect properties exist")
    public void theFollowingMultiselectPropertiesExist(DataTable dataTable) {
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = todoListRepository.findById(Integer.parseInt(row.get("todoListId"))).get();
            List<Property> existingProperties = todoList.getProperty();

            MultiSelectProperty multiSelectProperty = new MultiSelectProperty();
            multiSelectProperty.setName(row.get("name"));
            multiSelectProperty.setTodoList(todoList);

            List<String> valueNames = List.of(row.get("values").split(";"));
            List<PropertyValue> values = new ArrayList<>();
            for (String valueName : valueNames) {
                PropertyValue propertyValue = new PropertyValue(valueName);
                propertyValue.setProperty(multiSelectProperty);
                values.add(propertyValue);
            }
            multiSelectProperty.setValues(values);

            existingProperties.add(multiSelectProperty);
            propertyRepository.save(multiSelectProperty);

            todoList.setProperty(existingProperties);
            todoListRepository.save(todoList);
        }
    }

    @When("the user creates a literal property with name {string} for todoList with id {string}")
    public void theUserCreatesALiteralPropertyWithNameForTodoListWithId(String name, String todoListId) {
        LiteralPropertyDto newLiteralPropertyDto = new LiteralPropertyDto();
        newLiteralPropertyDto.setName(name);
        newLiteralPropertyDto.setTodoListId(Integer.parseInt(todoListId));
        controllerResponse = propertyController.createProperty(newLiteralPropertyDto);
    }

    @Then("a list does not exist error shall be thrown")
    public void aListDoesNotExistErrorShallBeThrown() {
            assertEquals(HttpStatus.NOT_FOUND, controllerResponse.getStatusCode());
    }

    @When("the user creates a multiselect property with name {string} for todoList with id {string} with values {string}")
    public void theUserCreatesAMultiselectPropertyWithNameForTodoListWithIdWithValues(String name, String todoListId, String valueStrings) {
        MultiselectPropertyDto newMultiselectPropertyDto = new MultiselectPropertyDto();
        newMultiselectPropertyDto.setName(name);
        newMultiselectPropertyDto.setTodoListId(Integer.parseInt(todoListId));

        List<String> listOfStrings = List.of(valueStrings.split(";"));
        for (String valueString: listOfStrings) {
            PropertyValue propertyValue = new PropertyValue(valueString);
            values.add(propertyValue);
        }
    }




    @Then("the todo with name {string} and description {string} exists with status {string} in the todo list {string}.")
    public void theTodoWithNameAndDescriptionAndStateNotDoneExists(String name, String description, String status, String todoListName) {
        TodoItem item = todoItemRepository.getByName(name);
        assertEquals(name, item.getName());
        assertEquals(description, item.getDescription());
        assertEquals(TodoItem.TodoStatus.valueOf(status), item.getStatus());
        assertEquals(todoListName, item.getTodoList().getName());
    }

    @Then("the following todos exist")
    public void theFollowingTodosExist(DataTable existingItems) {
        Iterable<TodoItem> todoItems = todoItemRepository.findAll();
        List<TodoItem> actualItems = new ArrayList<>();
        todoItems.forEach(actualItems::add);

        int row_number = 0;
        List<Map<String, String>> rows = existingItems.asMaps();
        for (var row: rows) {
            String expectedName = row.get("name");
            String expectedDescription = row.get("description");
            String expectedStatus = row.get("status");
            String todoListName = row.get("todo list");

            TodoItem actualItem = actualItems.get(row_number);

            assertEquals(expectedName, actualItem.getName());
            assertEquals(expectedDescription, actualItem.getDescription());
            assertEquals(TodoItem.TodoStatus.valueOf(expectedStatus), actualItem.getStatus());
            assertEquals(todoListName, actualItem.getTodoList().getName());

            row_number++;
        }
    }

    @Then("the following error message is returned: {string}")
    public void theFollowingErrorMessageIsReturned(String expectedMessage) {
        assertEquals(400, controllerResponse.getStatusCode().value());
        assertEquals(expectedMessage, controllerResponse.getBody());
    }










    @When("the user creates a multiselect property with name {string} for todoList with id {string}")
    public void theUserCreatesAMultiselectPropertyWithNameForTodoListWithId(String arg0, String arg1) {
    }

    @When("the user creates a multiselect property with name {string} for todo list with id {string}")
    public void theUserCreatesAMultiselectPropertyWithNameForTodoListWithId(String arg0, String arg1) {
    }
}
