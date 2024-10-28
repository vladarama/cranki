package ca.mcgill.cranki.repository;

import java.util.List;

import ca.mcgill.cranki.model.TodoItem;
import org.springframework.data.repository.CrudRepository;

import ca.mcgill.cranki.model.TodoList;

public interface TodoItemRepository extends CrudRepository<TodoItem, Integer> {
  TodoItem findByName(String name);
  
  List<TodoItem> findByTodoList(TodoList todoList);
}
