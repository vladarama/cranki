package ca.mcgill.cranki.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class LiteralProperty extends Property{
  @OneToOne(mappedBy = "property")
  private PropertyValue value;

  public LiteralProperty(String name) {
    super(name);
  }

  public LiteralProperty() {
  }
}
