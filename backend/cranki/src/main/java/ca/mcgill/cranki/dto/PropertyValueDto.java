package ca.mcgill.cranki.dto;

import ca.mcgill.cranki.model.PropertyValue;

public class PropertyValueDto {
    private int id;
    private String value;
    private int propertyId;

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public PropertyValueDto(PropertyValue v) {
        this.id = v.getId();
        this.value = v.getValue();
        this.propertyId = v.getProperty().getId();
    }
}
