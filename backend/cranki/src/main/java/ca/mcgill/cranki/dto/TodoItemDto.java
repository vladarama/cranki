package ca.mcgill.cranki.dto;

import ca.mcgill.cranki.model.TodoItem;

public class TodoItemDto {
  private int id;
  private String name;
  private TodoStatus status;
  private String description;
  private TodoPriority priority;

  public enum TodoStatus {
    NOT_DONE,
    IN_PROGRESS,
    DONE,
  }

  public enum TodoPriority {
    LOW,
    MEDIUM,
    HIGH,
  }

  public TodoItemDto() {
  }

  public TodoItemDto(
      TodoItem todoItem) {
    this.id = todoItem.getId();
    this.name = todoItem.getName();
    this.status = TodoStatus.valueOf(todoItem.getStatus().name());
    this.description = todoItem.getDescription();
    this.priority = TodoPriority.valueOf(todoItem.getPriority().name());
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPriority() {
    return priority != null ? priority.name() : TodoPriority.MEDIUM.name();
  }

  public TodoStatus getStatus() {
    return status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPriority(String priority) {
    try {
      this.priority = TodoPriority.valueOf(priority);
    } catch (IllegalArgumentException e) {
      this.priority = TodoPriority.MEDIUM;
    }
  }
}
