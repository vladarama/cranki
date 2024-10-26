package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.TodoItem;
import org.springframework.data.repository.CrudRepository;

public interface TodoItemRepository extends CrudRepository<TodoItem, Integer> {
  TodoItem getByName(String name);
}
