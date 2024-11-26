package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.dto.TodoDescriptionDto;
import ca.mcgill.cranki.dto.PropertyDto;
import ca.mcgill.cranki.dto.PropertyValueDto;
import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.dto.TodoItemPropertyValue;
import ca.mcgill.cranki.model.PropertyValue;
import ca.mcgill.cranki.model.SpecificProperty;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
public class TodoItemController {

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;
  @Autowired
  private PropertyValueRepository propertyValueRepository;
  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  private SpecificPropertyRepository specificPropertyRepository;

    @PostMapping(value = {"/todoLists/{todoListName}", "/todoLists/{todoListName}/"})
    public ResponseEntity<Object> createTodoItem(@RequestBody TodoItemDto todoItem, @PathVariable(name = "todoListName") String todoListName) {
        String name = todoItem.getName();
        String description = todoItem.getDescription();
        TodoList todoList = todoListRepository.getByName(todoListName);

        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Cannot create todo with empty name", HttpStatus.BAD_REQUEST);
        }
        if (todoList == null) {
            return new ResponseEntity<>("The todo list does not exist", HttpStatus.BAD_REQUEST);
        }
        if (description != null && description.length() > 2000) {
            return new ResponseEntity<>("Description exceeds maximum length of 2000 characters", HttpStatus.BAD_REQUEST);
        }

        TodoItem newItem = new TodoItem();
        newItem.setName(name);
        newItem.setDescription(description);
        newItem.setStatus(TodoItem.TodoStatus.NOT_DONE);
        newItem.setTodoList(todoList);
        todoItemRepository.save(newItem);

        TodoItemDto createdTodoItemDto = new TodoItemDto(newItem);
        return new ResponseEntity<>(createdTodoItemDto, HttpStatus.CREATED);
    }

    @PutMapping(value = { "/todoItem/updateStatus", "/todoItem/updateStatus/" })
    public ResponseEntity<String> updateTodoStatus(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "status") String status
    ) {
        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
        }
        var item = item_option.get();

        if (item.getStatus().equals(TodoItem.TodoStatus.valueOf(status))) {
            return new ResponseEntity<>("Task is already marked as " + status, HttpStatus.BAD_REQUEST);
        }
        item.setStatus(TodoItem.TodoStatus.valueOf(status));
        todoItemRepository.save(item);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping(value = { "/todoItem/{id}", "/todoItem/{id}/" })
    public ResponseEntity<TodoItemDto> getTodoItem(@PathVariable(name = "id") int id) {
        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TodoItem item = item_option.get();
        TodoItemDto todoItemDto = new TodoItemDto(item);

        var propertiesValue = item.getSpecificProperties().stream().map(s -> new TodoItemDto.TodoItemSpecificPropertyValues(s.getProperty().getId(), s.getProperty().getName(), PropertyDto.PropertyDtoType.parseType(s.getProperty()),s.getValues().stream().map(PropertyValueDto::new).toList()));
        todoItemDto.setPropertyValues(propertiesValue.toList());
        return new ResponseEntity<>(todoItemDto, HttpStatus.OK);
    }

    @PutMapping(value = {"/todoItem/{id}/properties/value", "/todoItem/{id}/properties/value/"})
    public ResponseEntity<Object> setTodoItemPropertyValue(@PathVariable(name = "id") int id, @RequestBody TodoItemPropertyValue value) {
        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TodoItem item = item_option.get();

        var property = propertyRepository.findById(value.propertyId()).orElseThrow();
        var specificProperty = new SpecificProperty();
        specificProperty.setTodoItem(item);
        specificProperty.setProperty(property);

      if (Objects.requireNonNull(value.type()) == PropertyDto.PropertyDtoType.LITERAL && value.valueId() == null) {
        var propertyValue = new PropertyValue();
        propertyValue.setValue(value.value());
        propertyValue.setProperty(property);
        propertyValueRepository.save(propertyValue);
        specificProperty.setValues(List.of(propertyValue));
      } else {
        if (value.type() == PropertyDto.PropertyDtoType.SINGLE_SELECT && value.valueId().size() > 1) {
          return new ResponseEntity<>("Cannot have more than one value for single select", HttpStatus.BAD_REQUEST);
        }
        specificProperty.setValues((List<PropertyValue>) propertyValueRepository.findAllById(value.valueId()));
      }
      specificPropertyRepository.save(specificProperty);
      var itemSpecificProperties = item.getSpecificProperties();
      itemSpecificProperties.add(specificProperty);
      item.setSpecificProperties(itemSpecificProperties);
      return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = { "/todoItem/{id}/description", "/todoItem/{id}/description/" })
    public ResponseEntity<String> getTodoItemDescription(@PathVariable(name = "id") int id) {
        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>("Todo is not found", HttpStatus.NOT_FOUND);
        }
        var item = item_option.get();
        return new ResponseEntity<>(item.getDescription(), HttpStatus.OK);
    }

    @PutMapping(value = { "/todoItem/{id}/updateDescription", "/todoItem/{id}/updateDescription/" })
    public ResponseEntity<String> editTodoDescription(
            @PathVariable(name = "id") int id,
            @RequestBody TodoDescriptionDto descriptionDto) {
        String description = descriptionDto.getDescription();
        if (description != null && description.length() > 2000) {
            return new ResponseEntity<>("Description exceeds maximum length of 2000 characters", HttpStatus.BAD_REQUEST);
        }
        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>("Todo is not found", HttpStatus.NOT_FOUND);
        }
        var item = item_option.get();
        item.setDescription(description);
        todoItemRepository.save(item);
        return new ResponseEntity<>("Todo item description updated successfully", HttpStatus.OK);
    }

    @PutMapping( value = { "/todoItem/updateName", "todoItem/updateName/" })
    public ResponseEntity<String> editTodoName(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "name") String name) {
        if (name.trim().isEmpty()) {
            return new ResponseEntity<>("Name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>("Todo is not found", HttpStatus.NOT_FOUND);
        }
        var item = item_option.get();
        item.setName(name.trim());
        todoItemRepository.save(item);

        return ResponseEntity.ok("Todo item name updated successfully");
    }

    @DeleteMapping(value = {"/todoItem/{id}", "/todoItem/{id}/"})
    public ResponseEntity<String> deleteTodoItem(@PathVariable(name = "id") int id) {
        var item = todoItemRepository.findById(id);
        if (item.isEmpty()) {
            return new ResponseEntity<>("Todo is not found", HttpStatus.NOT_FOUND);
        }
        todoItemRepository.deleteById(id);
        return new ResponseEntity<>("Todo item deleted successfully", HttpStatus.OK);
    }

    @GetMapping("/todoItems")
    public ResponseEntity<List<TodoItemDto>> getAllTodoItems() {
        List<TodoItem> items = (List<TodoItem>) todoItemRepository.findAll();
        List<TodoItemDto> itemDtos = items.stream()
            .map(TodoItemDto::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(itemDtos);
    }

    // MP
    @GetMapping("/todoItems/filter")
    public ResponseEntity<List<TodoItemDto>> filterTodosByProperty(
        @RequestParam(name = "property") String property,
        @RequestParam(name = "value", required = false) String value) {
        try {
            List<TodoItem> filteredTodos = new ArrayList<>(); 
            // Handle case where no value is provided
            if (value.trim().isEmpty()) {
                // Check if the property is valid and exists
                return ResponseEntity.ok(convert(convertIterableToList(todoItemRepository.findAll())));
            } else {
                // // Validate the property type
                // if (!property.equalsIgnoreCase("Category")) {
                //     throw new IllegalArgumentException("Unsupported property: " + property);
                // }
                // Perform filtering by category
                filteredTodos = todoItemRepository.findByProperty(value.trim());
                // Check if no results match the filter
                if (filteredTodos.isEmpty()) {
                    return ResponseEntity.ok(Collections.emptyList());
                }
            }
            // Convert entities to DTOs
            List<TodoItemDto> todoDtos = convert(filteredTodos);

            return ResponseEntity.ok(todoDtos);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<TodoItemDto> convert(List<TodoItem> filteredTodos) {
        // Create an empty list to store the converted TodoItemDto objects
        List<TodoItemDto> todoDtos = new ArrayList<>();
    
        // Iterate through each TodoItem in the input list
        for (TodoItem item : filteredTodos) {
            // Convert each TodoItem to a TodoItemDto
            TodoItemDto todoItemDto = new TodoItemDto(item);
            // Add the converted TodoItemDto to the output list
            todoDtos.add(todoItemDto);
        }
    
        // Return the list of TodoItemDto objects
        return todoDtos;
    }

    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }
    
}
