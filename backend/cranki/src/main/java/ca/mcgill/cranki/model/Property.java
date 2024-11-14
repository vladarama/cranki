package ca.mcgill.cranki.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract public class Property {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;
  private String Name;

  public Property(String name) {
    Name = name;
  }

  @ManyToOne
  @JoinColumn(name = "todo_list_id")
  private TodoList todoList;

  public Property() {

  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public TodoList getTodoList() {
    return todoList;
  }

  public void setTodoList(TodoList todoList) {
    this.todoList = todoList;
  }
}
