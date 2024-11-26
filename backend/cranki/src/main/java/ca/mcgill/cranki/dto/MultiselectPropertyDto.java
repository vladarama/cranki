package ca.mcgill.cranki.dto;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cranki.model.MultiSelectProperty;

public class MultiselectPropertyDto extends PropertyDto {
  List<Integer> valueIds;

  public MultiselectPropertyDto() {}

  public MultiselectPropertyDto(MultiSelectProperty property) {
    super(property);

    this.valueIds = new ArrayList<>();
  }

  public List<Integer> getValueIds() { return valueIds; }

  public void setValueIds(List<Integer> valueIds) { this.valueIds = valueIds; }

}