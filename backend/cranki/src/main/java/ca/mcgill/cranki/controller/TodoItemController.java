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
@RequestMapping(path = "/todoitem")
public class TodoItemController {
    @Autowired
    private TodoItemRepository todoItemRepository;

    @PutMapping()
    public ResponseEntity updateTodoStatus(@RequestBody TodoItemDto todoItemDto){
        var item = todoItemRepository.findById(todoItemDto.getId()).get();
        item.setStatus(TodoItem.TodoStatus.valueOf(todoItemDto.getStatus().name()));
        todoItemRepository.save(item);
        return new ResponseEntity(HttpStatus.OK);
    }



}