package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.controller.properties.PropertyController;
import ca.mcgill.cranki.dto.LiteralPropertyDto;
import ca.mcgill.cranki.dto.MultiselectPropertyDto;
import ca.mcgill.cranki.dto.PropertyDto;
import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.*;
import ca.mcgill.cranki.repository.PropertyRepository;
import ca.mcgill.cranki.repository.PropertyValueRepository;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.aspectj.weaver.ast.Literal;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    @Autowired
    private PropertyValueRepository propertyValueRepository;


    private void clearDatabase() {
        todoListRepository.deleteAll();
    }

    @AfterEach
    public void tearDownCreateProperty() {
        clearDatabase();
    }

    @Given("the following todo lists exist2")
    public void theFollowingTodoListsExist2(DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = new TodoList();
            todoList.setName(row.get("name"));
            todoListRepository.save(todoList);
        }
    }

    @And("the following literal properties already exist")
    public void theFollowingLiteralPropertiesExist(DataTable dataTable) {
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = todoListRepository.getByName(row.get("todoListName"));
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

    @And("the following multiselect properties already exist")
    public void theFollowingMultiselectPropertiesExist(DataTable dataTable) {
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = todoListRepository.getByName(row.get("todoListName"));
            List<Property> existingProperties = todoList.getProperty();

            MultiSelectProperty multiSelectProperty = new MultiSelectProperty();
            multiSelectProperty.setName(row.get("name"));
            multiSelectProperty.setTodoList(todoList);

            existingProperties.add(multiSelectProperty);
            propertyRepository.save(multiSelectProperty);

            todoList.setProperty(existingProperties);
            todoListRepository.save(todoList);
        }
    }

    @When("the user creates a literal property with name {string} for todo list with name {string}")
    public void theUserCreatesALiteralPropertyWithNameForTodoListWithName(String name, String todoListName) {
        LiteralPropertyDto newLiteralPropertyDto = new LiteralPropertyDto();
        newLiteralPropertyDto.setName(name);
        TodoList todoList = todoListRepository.getByName(todoListName);
        Integer todoListId;
        if (todoList == null) {
            todoListId = -1;
        } else {
            todoListId = todoList.getId();
        }
        newLiteralPropertyDto.setTodoListId(todoListId);
        newLiteralPropertyDto.setType(PropertyDto.PropertyDtoType.LITERAL);
        controllerResponse = propertyController.createProperty(newLiteralPropertyDto);
    }

    @Then("a list does not exist error shall be thrown")
    public void aListDoesNotExistErrorShallBeThrown() {
            assertEquals(HttpStatus.NOT_FOUND, controllerResponse.getStatusCode());
    }

    @When("the user creates a multiselect property with name {string} for todo list with name {string}")
    public void theUserCreatesAMultiselectPropertyWithNameForTodoListWithName(String name, String todoListName) {
        MultiselectPropertyDto newMultiselectPropertyDto = new MultiselectPropertyDto();
        newMultiselectPropertyDto.setName(name);
        newMultiselectPropertyDto.setType(PropertyDto.PropertyDtoType.MULTISELECT);
        TodoList todoList = todoListRepository.getByName(todoListName);
        Integer todoListId;
        if (todoList == null) {
           todoListId = -1;
        } else {
            todoListId = todoList.getId();
        }
        newMultiselectPropertyDto.setTodoListId(todoListId);
        List<Integer> propertyValueIds = new ArrayList<Integer>();
        newMultiselectPropertyDto.setValueIds(propertyValueIds);
        controllerResponse = propertyController.createProperty(newMultiselectPropertyDto);
        System.out.println("HEREEE " + controllerResponse.getStatusCode());
    }

    @Then("the following literal properties shall exist")
    public void theFollowingLiteralPropertiesShallExist(DataTable dataTable) {
        var rows = dataTable.asMaps();
        for (var row : rows) {
            String name = row.get("name");

            Property testprop = propertyRepository.getByName(name);
            assertInstanceOf(LiteralProperty.class, testprop);
            LiteralProperty litprop = (LiteralProperty) testprop;

            assertEquals(name, litprop.getName());
        }
    }

    @Then("the following multiselect properties shall exist")
    public void theFollowingMultiselectPropertiesShallExist(DataTable dataTable) {
        var rows = dataTable.asMaps();
        for (var row : rows) {
            String name = row.get("name");
            String todoListName = row.get("todoListName");

            Property testprop = propertyRepository.getByName(name);
            assertInstanceOf(MultiSelectProperty.class, testprop);
            MultiSelectProperty msprop = (MultiSelectProperty) testprop;

            assertEquals(name, msprop.getName());
            assertEquals(todoListName, msprop.getTodoList().getName());
        }
    }

}
