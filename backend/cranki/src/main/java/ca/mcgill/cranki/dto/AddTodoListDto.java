package ca.mcgill.cranki.dto;

public class AddTodoListDto {
  private String name;

  public AddTodoListDto() {
  }

  public AddTodoListDto(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
