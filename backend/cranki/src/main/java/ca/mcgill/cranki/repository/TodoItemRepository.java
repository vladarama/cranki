package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.TodoItem;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface TodoItemRepository extends CrudRepository<TodoItem, Integer> {
  TodoItem getByName(String name);

  //MP
  @Query("SELECT t FROM TodoItem t WHERE t.literalPropertyValue = :category")
    List<TodoItem> findByCategory(String category);
}
