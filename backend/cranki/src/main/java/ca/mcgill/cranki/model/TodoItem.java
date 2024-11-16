package ca.mcgill.cranki.model;

import jakarta.persistence.*;

@Entity
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private TodoStatus status;
  private TodoPriority priority;
  private String description;

  @ManyToOne
  @JoinColumn(name = "todo_list_id")
  private TodoList todoList;

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

  public TodoPriority getPriority() {
    return priority;
  }

  public void setPriority(TodoPriority priority) {
    this.priority = priority;
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
}
