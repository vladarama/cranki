package ca.mcgill.cranki.dto;

import ca.mcgill.cranki.model.*;

import java.util.List;

public class PropertyDto {
  private int id;
  private String name;
  private int todoListId;
  private PropertyDtoType type;
  private List<String> values;
  private List<PropertyValueDto> createdValues;

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  public List<PropertyValueDto> getCreatedValues() {
    return createdValues;
  }

  public void setCreatedValues(List<PropertyValueDto> createdValues) {
    this.createdValues = createdValues;
  }


  public enum PropertyDtoType {
    LITERAL,
    MULTISELECT,
    OTHER,
    SINGLE_SELECT;

    public static PropertyDtoType parseType(Property p) {
      switch (p) {
        case LiteralProperty ignored -> {
          return PropertyDtoType.LITERAL;
        }
        case MultiSelectProperty ignored -> {
          return PropertyDtoType.MULTISELECT;
        }
        case SingleSelectProperty ignored -> {
          return PropertyDtoType.SINGLE_SELECT;

        }
        default -> throw new RuntimeException("Type not expected");
      }
    }


  }

  public PropertyDto() {
  }

  public PropertyDto(Property property) {
    this.id = property.getId();
    this.name = property.getName();
    this.todoListId = property.getTodoList().getId();
    switch (property) {
      case LiteralProperty literalProperty -> {
        this.type = PropertyDtoType.LITERAL;
        var v = literalProperty.getValue();
        if (v != null) {
          this.createdValues = List.of(new PropertyValueDto(literalProperty.getValue()));
        }
      }
      case MultiSelectProperty multiSelectProperty -> {
        this.type = PropertyDtoType.MULTISELECT;
        var v = multiSelectProperty.getValues();
        if (v != null) {
          this.createdValues = multiSelectProperty.getValues().stream().map(PropertyValueDto::new).toList();
        }
      }
      case SingleSelectProperty singleSelectProperty -> {
        this.type = PropertyDtoType.SINGLE_SELECT;
        var v = singleSelectProperty.getValues();
        if (v != null) {
          this.createdValues = singleSelectProperty.getValues().stream().map(PropertyValueDto::new).toList();
        }
      }
      default -> this.type = PropertyDtoType.OTHER;
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

  public void setType(PropertyDtoType type) { this.type = type; }

  public PropertyDtoType getType() { return this.type; }
}