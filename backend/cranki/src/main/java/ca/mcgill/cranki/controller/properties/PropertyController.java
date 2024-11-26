package ca.mcgill.cranki.controller.properties;

import ca.mcgill.cranki.model.*;
import ca.mcgill.cranki.repository.PropertyValueRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.cranki.dto.PropertyDto;
import ca.mcgill.cranki.repository.PropertyRepository;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
public class PropertyController {
  @Autowired
  private PropertyRepository propertyRepository;
  @Autowired
  private TodoListRepository todoListRepository;
  @Autowired
  private PropertyValueRepository propertyValueRepository;

  @GetMapping(value = {"/property/{id}", "/property/{id}/"})
  public ResponseEntity<PropertyDto> getProperty(@PathVariable int id) {
    var property_option = propertyRepository.findById(id);
    if (property_option.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    var property = property_option.get();
    return new ResponseEntity<>(new PropertyDto(property), HttpStatus.OK);
  }

  @GetMapping(value = {"/property/{id}/values", "/property/{id}/values/"})
  public ResponseEntity<List<Integer>> getPropertyValues(@PathVariable int id) {
    var property_option = propertyRepository.findById(id);
    if (property_option.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    var property = property_option.get();
    if (property instanceof LiteralProperty) {
      List<Integer> emptyArray = new ArrayList<>();
      return new ResponseEntity<>(emptyArray, HttpStatus.OK);
    } else if (property instanceof MultiSelectProperty multiSelectProperty) {
      List<Integer> valueIds = multiSelectProperty.getValues().stream()
              .map(PropertyValue::getId)
              .toList();
      return new ResponseEntity<>(valueIds, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
  }

  @PostMapping(value = {"/property", "/property/"})
  public ResponseEntity<PropertyDto> createProperty(@RequestBody PropertyDto propertyDto) {
    var todoListOption = todoListRepository.findById(propertyDto.getTodoListId());
    if (todoListOption.isEmpty()) {
      return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
    TodoList todoList = todoListOption.get();

    Property property;

    List<PropertyValue> values = null;
    if (propertyDto.getValues() != null) {
      values = propertyDto.getValues().stream().map(PropertyValue::new).toList();
      propertyValueRepository.saveAll(values);
    }


    if (propertyDto.getType() == PropertyDto.PropertyDtoType.LITERAL) {
      property = new LiteralProperty(propertyDto.getName());
    } else if (propertyDto.getType() == PropertyDto.PropertyDtoType.MULTISELECT) {
      property = new MultiSelectProperty(propertyDto.getName(), values);
    } else if (propertyDto.getType() == PropertyDto.PropertyDtoType.SINGLE_SELECT) {
      var singleProperty = new SingleSelectProperty(propertyDto.getName());
      singleProperty.setValues(values);
      property = singleProperty;
    } else {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    property.setTodoList(todoList);
    List<Property> existingProperties = todoList.getProperty();
    existingProperties.add(property);
    todoList.setProperty(existingProperties);
    propertyRepository.save(property);
    if (values != null) {
      values.forEach(v -> v.setProperty(property));
      propertyValueRepository.saveAll(values);
    }

    propertyRepository.save(property);
    todoListRepository.save(todoList);
    return new ResponseEntity<>(new PropertyDto(property), HttpStatus.CREATED);
  }

  @PutMapping(value = {"/property/name ", "/property/name/"})
  public ResponseEntity<String> updateProperty(
          @RequestParam(name = "id") int id,
          @RequestParam(name = "name") String name) {
    if (name.trim().isEmpty()) {
      return new ResponseEntity<>("Name cannot be empty", HttpStatus.BAD_REQUEST);
    }

    var property_option = propertyRepository.findById(id);
    if (property_option.isEmpty()) {
      return new ResponseEntity<>("Property not found", HttpStatus.NOT_FOUND);
    }
    var property = property_option.get();

    TodoList todoList = property.getTodoList();
    List<Property> existingProperties = todoList.getProperty();
    existingProperties.add(property);
    todoList.setProperty(existingProperties);

    property.setName(name);
    propertyRepository.save(property);
    return ResponseEntity.ok("Property successfully updated");
  }

  @PutMapping(value = {"/property/{id}/value", "/property/{id}/value/"})
  public ResponseEntity<Object> updatePropertyValue(@PathVariable(name = "id") int id, @RequestParam(name = "value") String value) {
    var propertyValue= propertyValueRepository.findById(id).orElseThrow();
    propertyValue.setValue(value);
    propertyValueRepository.save(propertyValue);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // todo delete property step def
  // todo: create propertyvalue step def
  // (creation attaches to property)
  // todo delete propertyvalue step def
  // todo update propertyvalue step def
  // update attached values (specify new list of ids)
  // todo create specificproperty step def
  // create with list of value ids
}