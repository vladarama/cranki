package ca.mcgill.cranki.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private TodoStatus status;
  private String description;
  //filter todo
  private String literalPropertyValue; // for storing category

  @ManyToOne
  @JoinColumn(name = "todo_list_id")
  private TodoList todoList;

  @OneToMany(mappedBy = "todoItem")
  private Set<SpecificProperty> specificProperties;

  public Set<SpecificProperty> getSpecificProperties() {
    return specificProperties;
  }

  public void setSpecificProperties(Set<SpecificProperty> specificProperties) {
    this.specificProperties = specificProperties;
  }

  public enum TodoStatus {
    NOT_DONE,
    IN_PROGRESS,
    DONE,
  }

  // Default no-argument constructor for JPA
  public TodoItem() {
  }

  // Custom constructor with description (if needed), including id
  public TodoItem(int id, String name, TodoStatus status, String description) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.description = description;
  }

  // Getters and setters
  public TodoStatus getStatus() {
    return status;
  }

  public void setStatus(TodoStatus status) {
    this.status = status;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TodoList getTodoList() {
    return todoList;
  }

  public void setTodoList(TodoList todoList) {
    this.todoList = todoList;
  }

 // Getter and Setter for literalPropertyValue
    public String getLiteralPropertyValue() {
        return literalPropertyValue;
    }

    public void setLiteralPropertyValue(String literalPropertyValue) {
        this.literalPropertyValue = literalPropertyValue;
    }
}

