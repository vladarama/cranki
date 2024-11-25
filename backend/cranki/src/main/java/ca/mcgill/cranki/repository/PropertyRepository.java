package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.Property;
import ca.mcgill.cranki.model.TodoItem;
import org.springframework.data.repository.CrudRepository;

public interface PropertyRepository extends CrudRepository<Property, Integer> {
}
