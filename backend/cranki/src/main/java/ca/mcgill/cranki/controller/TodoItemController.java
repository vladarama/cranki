package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin(origins = "*")
@RestController
public class TodoItemController {

    @Autowired
    private TodoItemRepository todoItemRepository;

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
    public ResponseEntity<TodoItemDto> getTodoItem(@PathVariable(name = "id") int id) throws Exception {
        var item_option = todoItemRepository.findById(id);
        if (item_option.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        TodoItem item = item_option.get();
        TodoItemDto todoItemDto = new TodoItemDto(item);
        return new ResponseEntity<>(todoItemDto, HttpStatus.OK);
    }

    @PutMapping(value = { "/todoItem/updateName", "/todoItem/updateName/" })
    public ResponseEntity<String> editTodoName(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "name") String name
    ) {
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

    // @GetMapping(value = { "/todoItems", "/todoItems/" })
    // public ResponseEntity<List<TodoItemDto>> getAllTodoItems() {
    //     // Convert the Iterable returned by findAll() into a List
    //     List<TodoItem> items = StreamSupport
    //             .stream(todoItemRepository.findAll().spliterator(), false)
    //             .collect(Collectors.toList());

    //     List<TodoItemDto> itemDtos = items.stream()
    //             .map(TodoItemDto::new)
    //             .collect(Collectors.toList());

    //     return new ResponseEntity<>(itemDtos, HttpStatus.OK);
    // }

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
}
