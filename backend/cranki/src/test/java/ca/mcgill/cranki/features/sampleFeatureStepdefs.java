//package ca.mcgill.cranki.features;
//
//import ca.mcgill.cranki.controller.TodoListController;
//import ca.mcgill.cranki.dto.AddTodoListDto;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//import org.springframework.beans.factory.annotation.Autowired;
//import static org.junit.Assert.*;
//
//public class sampleFeatureStepdefs extends CucumberSpringConfiguration {
//  @Autowired
//  private TodoListController controller;
//
//
//
//  @Given("no todos have been created")
//  public void noTodosHaveBeenCreated() {
//  }
//
//
//  @When("requesting the creation of todo list with the following name {string}")
//  public void requestingTheCreationOfTodoListWithTheFollowingNameName(String name) {
//    controller.addTodoList(new AddTodoListDto(name));
//
//
//  }
//
//  @Then("the todo list with following name {string}")
//  public void theTodoListWithFollowingNameName(String name) {
//    assertEquals(controller.getTodoList(name).getBody(), name);
//  }
//}
