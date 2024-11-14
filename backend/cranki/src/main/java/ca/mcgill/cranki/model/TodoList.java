package ca.mcgill.cranki.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class TodoList {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  @Column(nullable = false)
  private String name;

  @OneToMany(mappedBy = "todoList")
  private List<TodoItem> items;

  @OneToMany()
  private List<Property> property;

  public TodoList(String name) {
    this.name = name;
  }

  public TodoList(String name, List<TodoItem> items) {
    this.name = name;
    this.items = items;
  }

  public TodoList() {

  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TodoItem> getItems() {
    return items;
  }

  public void setItems(List<TodoItem> items) {
    this.items = items;
  }

  public List<Property> getProperty() {
    return property;
  }

  public void setProperty(List<Property> property) {
    this.property = property;
  }
}
