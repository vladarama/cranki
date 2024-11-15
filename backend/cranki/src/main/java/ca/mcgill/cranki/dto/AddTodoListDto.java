package ca.mcgill.cranki.dto;
import ca.mcgill.cranki.model.TodoList;

public class AddTodoListDto {
  private String name;
  private int id;

  public AddTodoListDto() {
  }

  public AddTodoListDto(String name) {
    this.name = name;
  }

  public AddTodoListDto(String name, int id) {
    this.name = name;
    this.id = id;
  }

  public AddTodoListDto(TodoList todoList) {
    this.name = todoList.getName();
    this.id = todoList.getId();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
