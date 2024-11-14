package ca.mcgill.cranki.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class MultiSelectProperty extends Property {
  @OneToMany(mappedBy = "property")
  private List<PropertyValue> values;

  public MultiSelectProperty(String name, List<PropertyValue> values) {
    super(name);
    this.values = values;
  }

  public MultiSelectProperty() {
  }

  public List<PropertyValue> getValues() {
    return values;
  }

  public void setValues(List<PropertyValue> values) {
    this.values = values;
  }
}
