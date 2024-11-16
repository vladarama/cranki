package ca.mcgill.cranki.dto;

import ca.mcgill.cranki.model.LiteralProperty;
import ca.mcgill.cranki.model.MultiSelectProperty;
import ca.mcgill.cranki.model.Property;

abstract public class PropertyDto {
  private int id;
  private String name;
  private int todoListId;
  private PropertyDtoType type;

  public enum PropertyDtoType {
    LITERAL,
    MULTISELECT,
    OTHER
  }

  public PropertyDto() {
  }

  public PropertyDto(Property property) {
    this.id = property.getId();
    this.name = property.getName();
    this.todoListId = property.getTodoList().getId();
    if (property instanceof LiteralProperty) {
      this.type = PropertyDtoType.LITERAL;
    } else if (property instanceof MultiSelectProperty) {
      this.type = PropertyDtoType.MULTISELECT;
    } else {
      this.type = PropertyDtoType.OTHER;
    }
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTodoListId() {
    return todoListId;
  }

  public void setTodoListId(int newTodoListId) {
    this.todoListId = newTodoListId;
  }

  public PropertyDtoType getType() { return this.type; }
}