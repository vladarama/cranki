package ca.mcgill.cranki.unitTest;

import ca.mcgill.cranki.model.*;
import ca.mcgill.cranki.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@ContextConfiguration
public class PropertyStuffsTests {
  @Autowired
  private TodoListRepository todoListRepository;

  @Autowired
  private TodoItemRepository todoItemRepository;

  @Autowired
  private PropertyRepository propertyRepository;

  @Autowired
  PropertyValueRepository propertyValueRepository;

  @Autowired
  SpecificPropertyRepository specificPropertyRepository;


  @Test
  public void propertiesStuffWorks() {
    {
      var todoList = new TodoList();
      todoList.setName("Testingg");

      var todoItem = new TodoItem();
      todoItem.setName("Test todo item");
      todoListRepository.save(todoList);

      var multselectValues = List.of(new PropertyValue("Item 1"), new PropertyValue("Item 2"));
      propertyValueRepository.saveAll(multselectValues);
      var multselectProperty = new MultiSelectProperty("multiSelect", multselectValues);
      multselectValues.forEach(propertyValue -> {
        propertyValue.setProperty(multselectProperty);
      });
      var properties = List.of(new LiteralProperty("lala"), multselectProperty);
      propertyRepository.saveAll(properties);
      propertyValueRepository.saveAll(multselectValues);
      properties.forEach(property -> {
        property.setTodoList(todoList);
      });
      propertyRepository.saveAll(properties);


      todoList.setProperty(properties);
      todoListRepository.save(todoList);

      var litPropValue = new PropertyValue("lit value 1");
      litPropValue.setProperty(properties.getFirst());
      propertyValueRepository.save(litPropValue);
      var specificProperties = List.of(new SpecificProperty(properties.getFirst(), List.of(litPropValue)), new SpecificProperty(multselectProperty, List.of(multselectProperty.getValues().get(0))));
      todoItem.setSpecificProperties(new HashSet(specificProperties));
      todoItem.setTodoList(todoList);
      todoItemRepository.save(todoItem);
      specificProperties.forEach(specificProperty -> specificProperty.setTodoItem(todoItem));
      specificPropertyRepository.saveAll(specificProperties);
    }
    var todoItem = todoItemRepository.findById(1).get();

//    System.out.println(todoItem.getSpecificProperties().stream().map(specificProperty -> specificProperty.getValues().get(0).getValue()).collect(Collectors.joining()));

    System.out.println((long) todoItem.getSpecificProperties().size());
  }
}
