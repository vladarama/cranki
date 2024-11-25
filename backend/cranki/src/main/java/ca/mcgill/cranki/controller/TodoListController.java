package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.dto.AddTodoListDto;
import ca.mcgill.cranki.model.Property;
import ca.mcgill.cranki.model.TodoList;
import ca.mcgill.cranki.repository.TodoListRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  @PostMapping()
  public ResponseEntity addTodoList(@RequestBody AddTodoListDto data) {
    todoListRepository.save(new TodoList(data.getName()));
    return new ResponseEntity(HttpStatus.CREATED);
  }

  @GetMapping("{name}")
  public ResponseEntity<String> getTodoList(@PathVariable String name) {
    var res = todoListRepository.getByName(name);
    return new ResponseEntity<String>(res.getName(), HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<List<TodoList>> getTodoLists() {
    List<TodoList> todoLists = (List<TodoList>) todoListRepository.findAll();
    return ResponseEntity.ok(todoLists);
  }

  @GetMapping({"{name}/properties", "{name}/properties/"})
  public ResponseEntity<List<Integer>> getTodoListProperties(@PathVariable String name) {
    var todoList = todoListRepository.getByName(name);
    var properties = todoList.getProperty();
    List<Integer> propertyIds = properties.stream()
            .map(Property::getId)
            .toList();
    List<Integer> uniquePropertyIds = removeDuplicates(propertyIds);
    return new ResponseEntity<>(uniquePropertyIds, HttpStatus.OK);
  }


  private static <E> List<E> removeDuplicates (List<E> l) {
    Set<E> uniqueSet = new HashSet<>(l);
    return new ArrayList<>(uniqueSet);
  }
}