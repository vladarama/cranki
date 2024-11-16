package ca.mcgill.cranki.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class PropertyValue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  public PropertyValue(String value) {
    this.value = value;
  }

  private String value;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

  public PropertyValue() {

  }

  public int getId() { return id; }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }
}
