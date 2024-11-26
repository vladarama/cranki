package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.Property;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.model.TodoList;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PropertyRepository extends CrudRepository<Property, Integer> {
    Property getByName(String name);

    @Modifying
    @Query("UPDATE Property p SET p.todoList = NULL WHERE p.todoList IS NOT NULL")
    void updateAllPropertiesToRemoveTodoListAssociation();

    @Transactional
    void deleteAllByTodoListNotNull();
}
