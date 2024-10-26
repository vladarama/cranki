package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class TodoItemController {
    @Autowired
    private TodoItemRepository todoItemRepository;

    @PutMapping( value = { "/todoItem/updateStatus", "todoItem/updateStatus/" })
    public ResponseEntity<String> updateTodoStatus(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "status") String status
    ){
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

    @GetMapping(value = { "/todoItem/{id}", "/todoItem/{id}/"})
    public ResponseEntity<TodoItemDto> getTodoItem(@PathVariable(name = "id") int id) throws Exception {
        TodoItem item = todoItemRepository.findById(id).get();
        TodoItemDto todoItemDto = new TodoItemDto(item);
        return new ResponseEntity<>(todoItemDto, HttpStatus.OK);
    }

}