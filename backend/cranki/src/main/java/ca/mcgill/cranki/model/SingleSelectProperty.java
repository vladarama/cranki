package ca.mcgill.cranki.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class SingleSelectProperty extends Property {
  @OneToMany(mappedBy = "property", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<PropertyValue> values;

  public SingleSelectProperty(String name) {
    super(name);
  }

  public SingleSelectProperty() {

  }

  public List<PropertyValue> getValues() {
    return values;
  }

  public void setValues(List<PropertyValue> values) {
    this.values = values;
  }
}
