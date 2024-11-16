package ca.mcgill.cranki.dto;

import java.util.List;

import ca.mcgill.cranki.model.MultiSelectProperty;
import ca.mcgill.cranki.model.PropertyValue;

public class MultiselectPropertyDto extends PropertyDto {
  List<PropertyValue> values;

  public MultiselectPropertyDto() {}

  public MultiselectPropertyDto(MultiSelectProperty property) {
    super(property);
    this.values = property.getValues();
  }

}