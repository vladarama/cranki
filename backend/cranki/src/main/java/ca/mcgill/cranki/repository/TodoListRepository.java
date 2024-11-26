package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.TodoList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoListRepository extends CrudRepository<TodoList, Integer> {
  TodoList getByName(String name);
}