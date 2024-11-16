package ca.mcgill.cranki.controller.properties;

import ca.mcgill.cranki.dto.LiteralPropertyDto;
import ca.mcgill.cranki.dto.MultiselectPropertyDto;
import ca.mcgill.cranki.model.*;
import ca.mcgill.cranki.repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.cranki.dto.PropertyDto;
import ca.mcgill.cranki.repository.PropertyRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@CrossOrigin(origins = "*")
@RestController
public class PropertyController {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private TodoListRepository todoListRepository;

    @GetMapping(value = {"/property/{id}", "/property/{id}/"})
    public ResponseEntity<PropertyDto> getProperty(@PathVariable int id) {
        var property_option = propertyRepository.findById(id);
        if (property_option.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var property = property_option.get();
        if (property instanceof LiteralProperty literalProperty) {
            LiteralPropertyDto literalPropertyDto = new LiteralPropertyDto(literalProperty);
            return new ResponseEntity<>(literalPropertyDto, HttpStatus.OK);
        } else if (property instanceof MultiSelectProperty multiSelectProperty) {
            MultiselectPropertyDto multiselectPropertyDto = new MultiselectPropertyDto(multiSelectProperty);
            return new ResponseEntity<>(multiselectPropertyDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
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
    public ResponseEntity<Object> createProperty(@RequestBody PropertyDto propertyDto) {
        var todoListOption = todoListRepository.findById(propertyDto.getTodoListId());
        if (todoListOption.isEmpty()){
            return new ResponseEntity<>("No todo list found for id.", HttpStatus.NOT_FOUND);
        }
        TodoList todoList = todoListOption.get();

        Property property;

        if (propertyDto.getType() == PropertyDto.PropertyDtoType.LITERAL) {
            property = new LiteralProperty(propertyDto.getName());
        } else if (propertyDto.getType() == PropertyDto.PropertyDtoType.MULTISELECT) {
            property = new MultiSelectProperty(propertyDto.getName(), new ArrayList<>());
        } else {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        property.setTodoList(todoList);
        List<Property> existingProperties = todoList.getProperty();
        existingProperties.add(property);
        todoList.setProperty(existingProperties);

        propertyRepository.save(property);
        todoListRepository.save(todoList);
        return new ResponseEntity<>(property, HttpStatus.CREATED);
    }

}