package ca.mcgill.cranki.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class LiteralProperty extends Property{
  @OneToOne(mappedBy = "property", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private PropertyValue value;

  public LiteralProperty(String name) {
    super(name);
  }

  public LiteralProperty() {
  }

  public PropertyValue getValue() {
    return value;
  }

  public void setValue(PropertyValue value) {
    this.value = value;
  }
}
