package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.PropertyValue;
import ca.mcgill.cranki.model.SpecificProperty;
import org.springframework.data.repository.CrudRepository;

public interface PropertyValueRepository extends CrudRepository<PropertyValue, Integer> {
}
