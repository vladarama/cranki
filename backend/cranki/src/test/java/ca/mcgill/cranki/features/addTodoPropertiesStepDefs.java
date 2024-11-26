package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.controller.properties.PropertyController;
import ca.mcgill.cranki.dto.PropertyDto;
import ca.mcgill.cranki.dto.PropertyValueDto;
import ca.mcgill.cranki.dto.TodoItemPropertyValue;
import ca.mcgill.cranki.model.LiteralProperty;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.PropertyRepository;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class addTodoPropertiesStepDefs {
  @Autowired
  private TodoItemRepository todoItemRepository;

  @Autowired
  private TodoListRepository todoListRepository;

  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  private TodoItemController todoItemController;

  @Autowired
  private PropertyController propertyController;

  private TodoItem createdTodoItem;

  private String error;

  @Before
  @After
  public void clearDb() {
    todoListRepository.deleteAll();
  }


  @Given("the User is on the todo list page")
  public void theUserIsOnTheTodoListPage() {
  }

  @And("there is a todo {string}")
  public void thereIsATodo(String arg0) {
    var todoList = new TodoList();
    todoList.setName("lala");
    todoListRepository.save(todoList);
    var todoItem = new TodoItem();
    todoItem.setName(arg0);
    todoItem.setStatus(TodoItem.TodoStatus.NOT_DONE);
    todoItem.setTodoList(todoList);
    todoItemRepository.save(todoItem);
    createdTodoItem = todoItem;
  }

  @When("the User selects the todo with {string}")
  public void theUserSelectsTheTodoWith(String arg0) {
    assertTrue(true);
  }

  @And("the User adds the single-select property {string} with the values {string}, {string}, and {string}")
  public void theUserAddsTheSingleSelectPropertyWithTheValuesAnd(String arg0, String arg1, String arg2, String arg3) {
    var data = new PropertyDto();
    data.setName(arg0);
    data.setValues(List.of(arg1, arg2, arg3));
    data.setTodoListId(createdTodoItem.getTodoList().getId());
    data.setType(PropertyDto.PropertyDtoType.SINGLE_SELECT);
    var res = propertyController.createProperty(data);

    assertTrue(res.getStatusCode().is2xxSuccessful());
  }

  @And("the User selects {string} for the property {string}")
  public void theUserSelectsForTheProperty(String arg0, String arg1) {
    var property = new PropertyDto(propertyRepository.getByName(arg1));

    todoItemController.setTodoItemPropertyValue(createdTodoItem.getId(), new TodoItemPropertyValue(property.getId(),
            property.getType(), List.of(property.getCreatedValues().stream().filter(v -> Objects.equals(v.getValue(), arg0)).findFirst().orElseThrow().getId()), null));
  }

  @Then("the todo {string} should be displayed with the value {string} for the property {string}")
  public void theTodoShouldBeDisplayedWithTheValueForTheProperty(String arg0, String arg1, String arg2) {
    var req = todoItemController.getTodoItem(createdTodoItem.getId());
    assertTrue(req.getStatusCode().is2xxSuccessful());

    var res = req.getBody();
    assertNotNull(res);
    assertEquals(res.getName(), arg0);

    assertTrue(res.getPropertyValues().stream().filter(v -> Objects.equals(v.name(), arg2)).findFirst().orElseThrow().values().stream().anyMatch(v -> Objects.equals(v.getValue(), arg1)));
  }

  @Then("the todo {string} should be displayed with the property {string} with no value")
  public void theTodoShouldBeDisplayedWithThePropertyWithNoValue(String arg0, String arg1) {
    var req = todoItemController.getTodoItem(createdTodoItem.getId());
    assertTrue(req.getStatusCode().is2xxSuccessful());

    var res = req.getBody();
    assertNotNull(res);
    assertEquals(res.getName(), arg0);

    assertTrue(res.getPropertyValues().stream().filter(v -> Objects.equals(v.name(), arg0)).findFirst().isEmpty());
  }

  @And("the single-select property {string} exists with the values {string}, {string}, and {string}")
  public void theSingleSelectPropertyExistsWithTheValuesAnd(String arg0, String arg1, String arg2, String arg3) {
    var data = new PropertyDto();
    data.setName(arg0);
    data.setValues(List.of(arg1, arg2, arg3));
    data.setTodoListId(createdTodoItem.getTodoList().getId());
    data.setType(PropertyDto.PropertyDtoType.SINGLE_SELECT);
    var res = propertyController.createProperty(data);

    assertTrue(res.getStatusCode().is2xxSuccessful());
  }

  @And("the User selects {string} and {string} for the property {string}")
  public void theUserSelectsAndForTheProperty(String arg0, String arg1, String arg2) {
    var property = new PropertyDto(propertyRepository.getByName(arg2));

    var req = todoItemController.setTodoItemPropertyValue(createdTodoItem.getId(), new TodoItemPropertyValue(property.getId(),
            property.getType(), property.getCreatedValues().stream().filter(v -> Objects.equals(v.getValue(), arg0) || Objects.equals(v.getValue(), arg1)).map(PropertyValueDto::getId).toList(), null));

    if (req.getStatusCode() == HttpStatus.BAD_REQUEST) {
      error = Objects.requireNonNull(req.getBody()).toString();
    } else {
      assertTrue(req.getStatusCode().is2xxSuccessful());
    }
  }

  @Then("the system should display an error indicating that single-select properties only allow one value")
  public void theSystemShouldDisplayAnErrorIndicatingThatSingleSelectPropertiesOnlyAllowOneValue() {
    assertEquals(error, "Cannot have more than one value for single select");
  }

  @And("the multi-select property {string} exists with the values {string}, {string}, and {string}")
  public void theMultiSelectPropertyExistsWithTheValuesAnd(String arg0, String arg1, String arg2, String arg3) {
    var data = new PropertyDto();
    data.setName(arg0);
    data.setValues(List.of(arg1, arg2, arg3));
    data.setTodoListId(createdTodoItem.getTodoList().getId());
    data.setType(PropertyDto.PropertyDtoType.MULTISELECT);
    var res = propertyController.createProperty(data);

    assertTrue(res.getStatusCode().is2xxSuccessful());
  }

  @Then("the todo {string} should be displayed with the value {string} and {string} for the property {string}")
  public void theTodoShouldBeDisplayedWithTheValueAndForTheProperty(String arg0, String arg1, String arg2, String arg3) {
    var req = todoItemController.getTodoItem(createdTodoItem.getId());
    assertTrue(req.getStatusCode().is2xxSuccessful());

    var res = req.getBody();
    assertNotNull(res);
    assertEquals(res.getName(), arg0);

    assertTrue(res.getPropertyValues().stream().filter(v -> Objects.equals(v.name(), arg3)).findFirst().orElseThrow().values().stream().allMatch(v -> Objects.equals(v.getValue(), arg1) || Objects.equals(v.getValue(), arg2)));
  }

  @Given("I am on the todo list page")
  public void iAmOnTheTodoListPage() {
    assert true;
  }

  @When("I add a new todo with the text {string}")
  public void iAddANewTodoWithTheText(String arg0) {
    var todoList = new TodoList();
    todoList.setName("lala");
    todoListRepository.save(todoList);
    var todoItem = new TodoItem();
    todoItem.setName(arg0);
    todoItem.setStatus(TodoItem.TodoStatus.NOT_DONE);
    todoItem.setTodoList(todoList);
    todoItemRepository.save(todoItem);
    createdTodoItem = todoItem;
  }

  @And("I add the property {string}")
  public void iAddTheProperty(String arg0) {
    var data = new PropertyDto();
    data.setName(arg0);
    data.setTodoListId(createdTodoItem.getTodoList().getId());
    data.setType(PropertyDto.PropertyDtoType.LITERAL);
    var res = propertyController.createProperty(data);

    assertTrue(res.getStatusCode().is2xxSuccessful());
  }

  @And("I click the {string} button")
  public void iClickTheButton(String arg0) {
    assert true;
  }

  @And("there is a todo {string} with the property {string}")
  public void thereIsATodoWithTheProperty(String arg0, String arg1) {
    var req = todoItemController.getTodoItem(createdTodoItem.getId());
    assertTrue(req.getStatusCode().is2xxSuccessful());

    var res = req.getBody();
    assertNotNull(res);
    assertEquals(res.getName(), arg0);

    assertEquals(res.getPropertyValues().stream().filter(v -> Objects.equals(v.name(), arg0)).findFirst().orElseThrow().name(), arg1);
  }

  @When("I select the todo with {string}")
  public void iSelectTheTodoWith(String arg0) {
    assert true;
  }

  @And("I edit the property {string} to {string}")
  public void iEditThePropertyTo(String arg0, String arg1) {
    var req = propertyController.updateProperty(propertyRepository.getByName(arg0).getId(), arg1);
    assertTrue(req.getStatusCode().is2xxSuccessful());
  }

  @And("the literal property {string} exists")
  public void theLiteralPropertyExists(String arg0) {
    var data = new PropertyDto();
    data.setName(arg0);
    data.setTodoListId(createdTodoItem.getTodoList().getId());
    data.setType(PropertyDto.PropertyDtoType.LITERAL);
    var res = propertyController.createProperty(data);

    assertTrue(res.getStatusCode().is2xxSuccessful());
  }

  @And("there is a todo {string} with the property {string} with value {string}")
  public void thereIsATodoWithThePropertyWithValue(String arg0, String arg1, String arg2) {
    var todoList = new TodoList();
    todoList.setName("lala");
    todoListRepository.save(todoList);
    var todoItem = new TodoItem();
    todoItem.setName(arg0);
    todoItem.setStatus(TodoItem.TodoStatus.NOT_DONE);
    todoItem.setTodoList(todoList);
    todoItemRepository.save(todoItem);
    createdTodoItem = todoItem;

    var data = new PropertyDto();
    data.setName(arg1);
    data.setTodoListId(createdTodoItem.getTodoList().getId());
    data.setValues(List.of(arg2));
    data.setType(PropertyDto.PropertyDtoType.LITERAL);
    var res = propertyController.createProperty(data);

    assertTrue(res.getStatusCode().is2xxSuccessful());
    var property = res.getBody();

    var req = todoItemController.setTodoItemPropertyValue(createdTodoItem.getId(), new TodoItemPropertyValue(property.getId(),
            property.getType(), null, arg2));

    assertTrue(req.getStatusCode().is2xxSuccessful());
  }

  @And("I add the property value {string} for property {string}")
  public void iAddThePropertyValueForProperty(String arg0, String arg1) {
    var property = new PropertyDto(propertyRepository.getByName(arg1));

    var req = todoItemController.setTodoItemPropertyValue(createdTodoItem.getId(), new TodoItemPropertyValue(property.getId(),
            property.getType(),null , arg0));

      assertTrue(req.getStatusCode().is2xxSuccessful());
  }

  @And("I edit the property value to {string} for property {string}")
  public void iEditThePropertyValueToForProperty(String arg0, String arg1) {
    var property = (LiteralProperty) propertyRepository.getByName(arg1);
    var req = propertyController.updatePropertyValue(property.getValue().getId(), arg0);
    assertTrue(req.getStatusCode().is2xxSuccessful());
  }
}
