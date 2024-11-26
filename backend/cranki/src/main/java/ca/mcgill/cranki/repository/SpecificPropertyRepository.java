package ca.mcgill.cranki.repository;

import ca.mcgill.cranki.model.Property;
import ca.mcgill.cranki.model.SpecificProperty;
import org.springframework.data.repository.CrudRepository;

public interface SpecificPropertyRepository extends CrudRepository<SpecificProperty, Integer> {
}
