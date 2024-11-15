package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.dto.AddTodoListDto;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.TodoListRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/todolist")
public class TodoListController {
  @Autowired
  private TodoListRepository todoListRepository;

  @PostMapping(value = {"/todoLists", "/todoLists/"})
  public ResponseEntity<Object> createTodoList(@RequestBody AddTodoListDto data) {

    if (data.getName() == null || data.getName().trim().isEmpty()){
      return new ResponseEntity<>("Cannot create todo list with empty name", HttpStatus.BAD_REQUEST);
    }

    todoListRepository.save(new TodoList(data.getName()));
    return new ResponseEntity<>(HttpStatus.CREATED);

  }

  @GetMapping(value={"{name}", "{name}/"})
  public ResponseEntity<AddTodoListDto> getTodoList(@PathVariable String name) {
    var res = todoListRepository.getByName(name);

    if (res == null){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    AddTodoListDto todoListDto = new AddTodoListDto(res);

    return new ResponseEntity<AddTodoListDto>(todoListDto, HttpStatus.OK);
  }

  @GetMapping(value={"{id}", "{id}/"})
  public ResponseEntity<AddTodoListDto> getTodoList(@PathVariable(name = "id") int id) {
    var item_option = todoListRepository.findById(id);

    if (item_option.isEmpty()){
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    var item = item_option.get();
    AddTodoListDto todoListDto = new AddTodoListDto(item);

    return new ResponseEntity<AddTodoListDto>(todoListDto, HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<List<AddTodoListDto>> getTodoLists() {
    List<TodoList> todoLists = (List<TodoList>) todoListRepository.findAll();
    List<AddTodoListDto> todoListItems = todoLists.stream()
    .map(AddTodoListDto::new)
    .collect(Collectors.toList());
    return ResponseEntity.ok(todoListItems);
  }

  
  @PutMapping( value = { "{id}", "{id}/" })
  public ResponseEntity<String> editTodoListName(
    @PathVariable(name = "id") int id,
    @RequestParam(name = "name") String name){
    if (name.trim().isEmpty()) {
        return new ResponseEntity<>("Name cannot be empty", HttpStatus.BAD_REQUEST);
    }

    var item_option = todoListRepository.findById(id);
    if (item_option.isEmpty()) {
        return new ResponseEntity<>("Todo is not found", HttpStatus.NOT_FOUND);
    }

    var item = item_option.get();
    item.setName(name.trim());
    todoListRepository.save(item);

    return ResponseEntity.ok("Todo list name updated successfully");
  }


  @DeleteMapping(value={"{name}", "{name}/"})
  public ResponseEntity<String> deleteTodoList(@PathVariable String name) {
    var res = todoListRepository.getByName(name);

    if (res == null){
      return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
    }

    todoListRepository.delete(res);

    return new ResponseEntity<>("Todo list deleted successfully", HttpStatus.OK);
  }

  @DeleteMapping(value={"{id}", "{id}/"})
  public ResponseEntity<String> deleteTodoList(@PathVariable(name = "id") int id) {
    var item_option = todoListRepository.findById(id);

    if (item_option.isEmpty()){
      return new ResponseEntity<>("Task not found", HttpStatus.BAD_REQUEST);
    }

    var item = item_option.get();
    todoListRepository.delete(item);

    return new ResponseEntity<>("Todo list deleted successfully", HttpStatus.OK);
  }

}
