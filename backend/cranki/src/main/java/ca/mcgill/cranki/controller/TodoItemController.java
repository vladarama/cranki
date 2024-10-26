package ca.mcgill.cranki.controller;

import ca.mcgill.cranki.repository.TodoItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/todoitem")
public class TodoItemController {
    @Autowired
    private TodoItemRepository todoItemRepository;


}