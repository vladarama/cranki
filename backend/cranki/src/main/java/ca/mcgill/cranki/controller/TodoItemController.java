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

    @PutMapping( value = { "/todoItem/updateStatus" })
    public ResponseEntity<TodoItemDto> updateTodoStatus(
            @RequestParam(name = "id") int id,
            @RequestParam(name = "status") String status
    ){
        var item = todoItemRepository.findById(id).get();
        System.out.println("IN IN IN1: " + item.getStatus() + item.getName());
        item.setStatus(TodoItem.TodoStatus.valueOf(status));
        todoItemRepository.save(item);
        System.out.println("IN IN IN2: " + todoItemRepository.findById(id).get().getStatus());
        return new ResponseEntity<>(HttpStatus.OK);
    }



}