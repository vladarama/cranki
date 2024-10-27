package ca.mcgill.cranki.model;

import jakarta.persistence.*;

@Entity
public class TodoItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private TodoStatus status;
  private String description;

  @ManyToOne
  @JoinColumn(name = "todo_list_id")
  private TodoList todoList;


  public enum TodoStatus {
    NOT_DONE,
    IN_PROGRESS,
    DONE,
  }

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

}
