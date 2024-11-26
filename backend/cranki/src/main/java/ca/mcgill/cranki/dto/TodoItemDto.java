package ca.mcgill.cranki.dto;

import ca.mcgill.cranki.model.TodoItem;

import java.util.List;

public class TodoItemDto {
  private int id;
  private String name;
  private TodoStatus status;
  private String description;
  private List<TodoItemSpecificPropertyValues> propertyValues;

  public List<TodoItemSpecificPropertyValues> getPropertyValues() {
    return propertyValues;
  }

  public void setPropertyValues(List<TodoItemSpecificPropertyValues> propertyValues) {
    this.propertyValues = propertyValues;
  }


  public record TodoItemSpecificPropertyValues(int id, String name, PropertyDto.PropertyDtoType type, List<PropertyValueDto> values){}

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
    this.status = todoItem.getStatus() != null ? TodoStatus.valueOf(todoItem.getStatus().name()) : TodoStatus.NOT_DONE;
    this.description = todoItem.getDescription();
  }

  public int getId() { return id;}

  public String getName() {
    return name;
  }

  public TodoStatus getStatus() { return status; }

  public String getDescription() { return description; }

  public void setDescription(String description) { this.description = description; }

  public void setName(String name) {
    this.name = name;
  }
}
