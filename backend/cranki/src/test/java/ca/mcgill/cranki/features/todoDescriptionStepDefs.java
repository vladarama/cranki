package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.dto.TodoDescriptionDto;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class todoDescriptionStepDefs {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<String> controllerResponse;

    private TodoItem testTodoItem;

    private void clearDatabase() {
        todoItemRepository.deleteAll();
        todoListRepository.deleteAll();
    }

    @Given("the following todo lists exist in the system")
    public void theFollowingTodoListsExistInTheSystem(DataTable dataTable) {
        clearDatabase();
        var rows = dataTable.asMaps();
        for (var row : rows) {
            TodoList todoList = new TodoList();
            todoList.setName(row.get("name"));
            todoListRepository.save(todoList);
        }
    }

    @Given("I have an existing todo item {string} with no description:")
    public void iHaveAnExistingTodoItemWithNoDescription(String todoName) {
        TodoList todoList = todoListRepository.getByName("House Keeping");
        testTodoItem = new TodoItem();
        testTodoItem.setName(todoName);
        testTodoItem.setTodoList(todoList);
        todoItemRepository.save(testTodoItem);
    }

    @Given("I have an existing todo item {string} with description {string}:")
    public void iHaveAnExistingTodoItemWithDescription(String todoName, String description) {
        TodoList todoList = todoListRepository.getByName("House Keeping");
        testTodoItem = new TodoItem();
        testTodoItem.setName(todoName);
        testTodoItem.setDescription(description);
        testTodoItem.setTodoList(todoList);
        todoItemRepository.save(testTodoItem);
    }

    @Given("I have no todo items")
    public void iHaveNoTodoItems() {
        todoItemRepository.deleteAll();
    }

    @When("I add a description {string} to the todo item")
    public void iAddADescriptionToTheTodoItem(String description) {
        TodoDescriptionDto todoDescriptionDto = new TodoDescriptionDto();
        todoDescriptionDto.setDescription(description);
        controllerResponse = todoItemController.editTodoDescription(testTodoItem.getId(), todoDescriptionDto);
    }

    @When("I change the description to {string}")
    public void iChangeTheDescriptionTo(String description) {
        TodoDescriptionDto todoDescriptionDto = new TodoDescriptionDto();
        todoDescriptionDto.setDescription(description);
        controllerResponse = todoItemController.editTodoDescription(testTodoItem.getId(), todoDescriptionDto);
    }
}
