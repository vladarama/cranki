package ca.mcgill.cranki.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class SpecificProperty {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  @JoinColumn(name = "todo_item_id")
  private TodoItem todoItem;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

  @ManyToMany
  @JoinTable(name = "specific_property_value",
          joinColumns = @JoinColumn(name = "value_id"),
          inverseJoinColumns = @JoinColumn(name = "specific_property_id"))
  private List<PropertyValue> values;

  public SpecificProperty(Property property, List<PropertyValue> values) {
    this.property = property;
    this.values = values;
  }

  public SpecificProperty() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public TodoItem getTodoItem() {
    return todoItem;
  }

  public void setTodoItem(TodoItem todoItem) {
    this.todoItem = todoItem;
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public List<PropertyValue> getValues() {
    return values;
  }

  public void setValues(List<PropertyValue> values) {
    this.values = values;
  }

}
