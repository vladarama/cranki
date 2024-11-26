package ca.mcgill.cranki.dto;

import java.util.List;

public record TodoItemPropertyValue(int propertyId, PropertyDto.PropertyDtoType type, List<Integer> valueId, String value) {
}
