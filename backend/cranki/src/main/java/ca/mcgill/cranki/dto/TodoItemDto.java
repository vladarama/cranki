package ca.mcgill.cranki.dto;

public class TodoItemDto {
  private int id;
  private String name;
  private TodoStatus status;
  private String description;

  public enum TodoStatus {
    NOT_DONE,
    IN_PROGRESS,
    DONE,
  }

  public TodoItemDto() {
  }

  public TodoItemDto(
    TodoItem todoItem
  ) {
    this.id = todoItem.getId();
    this.name = todoItem.getName();
    this.status = TodoStatus.valueOf(todoItem.getStatus().name());
    this.description = todoItem.getDescription();
  }

  public int getId() { return id;}

  public String getName() {
    return name;
  }

  public TodoStatus getStatus() { return status; }

  public String getDescription() { return description; }

  public void setName(String name) {
    this.name = name;
  }
}
