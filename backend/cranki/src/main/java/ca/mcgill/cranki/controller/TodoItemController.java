package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.dto.TodoItemDto;
import ca.mcgill.cranki.model.TodoItem;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class TodoItemController {
    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @PostMapping(value = {"/todoLists/{todoListName}", "/todoLists/{todoListName}/"})
    public ResponseEntity<String> createTodoItem(@RequestBody TodoItemDto todoItem, @PathVariable(name = "todoListName") String todoListName) {
        String name = todoItem.getName();
        String description = todoItem.getDescription();
        TodoList todoList = todoListRepository.getByName(todoListName);
        TodoItem possibleTodoItem = todoItemRepository.getByName(name);

        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("Cannot create todo with empty name", HttpStatus.BAD_REQUEST);
        }
        if (possibleTodoItem != null && possibleTodoItem.getTodoList().getName().equals(todoListName)) {
            return new ResponseEntity<>("Todo with the same name already exists", HttpStatus.BAD_REQUEST);
        }
        if (todoList == null) {
            return new ResponseEntity<>("The todo list does not exist", HttpStatus.BAD_REQUEST);
        }

        TodoItem newItem = new TodoItem(name, description, TodoItem.TodoStatus.NOT_DONE, todoList);
        todoItemRepository.save(newItem);
        return new ResponseEntity<>("Todo item created successfully", HttpStatus.CREATED);
    }

    @PutMapping(value = {"/todoItem/updateStatus", "todoItem/updateStatus/"})
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
        return new ResponseEntity<>("Task status updated to " + status, HttpStatus.OK);
    }

    @GetMapping(value = {"/todoItem/{id}", "/todoItem/{id}/"})
    public ResponseEntity<TodoItemDto> getTodoItem(@PathVariable(name = "id") int id) throws Exception {
        TodoItem item = todoItemRepository.findById(id).get();
        TodoItemDto todoItemDto = new TodoItemDto(item);
        return new ResponseEntity<>(todoItemDto, HttpStatus.OK);
    }

}