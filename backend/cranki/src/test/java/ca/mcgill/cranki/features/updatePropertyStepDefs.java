package ca.mcgill.cranki.features;

import ca.mcgill.cranki.controller.TodoItemController;
import ca.mcgill.cranki.controller.properties.PropertyController;
import ca.mcgill.cranki.dto.LiteralPropertyDto;
import ca.mcgill.cranki.dto.MultiselectPropertyDto;
import ca.mcgill.cranki.dto.PropertyDto;
import ca.mcgill.cranki.model.*;
import ca.mcgill.cranki.repository.PropertyRepository;
import ca.mcgill.cranki.repository.PropertyValueRepository;
import ca.mcgill.cranki.repository.TodoItemRepository;
import ca.mcgill.cranki.repository.TodoListRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class updatePropertyStepDefs {
    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyController propertyController;

    @Autowired
    private TodoItemRepository todoItemRepository;

    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoItemController todoItemController;

    private ResponseEntity<String> controllerResponse;
    @Autowired
    private PropertyValueRepository propertyValueRepository;


    private void clearDatabase() {
        todoListRepository.deleteAll();
        propertyRepository.deleteAll();
        propertyValueRepository.deleteAll();
    }

    @BeforeEach
    public void setupUpdateProperty() {
        clearDatabase();
    }

    @AfterEach
    public void tearDownCreateProperty() {
        clearDatabase();
    }

    @And("property with name {string} has values")
    public void propertyWithNameHasValues(String name, DataTable dataTable) {
       MultiSelectProperty msprop = (MultiSelectProperty) propertyRepository.getByName(name);

       List<PropertyValue> newValues = new ArrayList<>();
       var rows = dataTable.asMaps();
       for (var row: rows) {
           PropertyValue newpv = new PropertyValue(row.get("value"));
           newValues.add(newpv);
           newpv.setProperty(msprop);
           propertyValueRepository.save(newpv);
       }
       msprop.setValues(newValues);
       propertyRepository.save(msprop);
    }

    @When("the user changes the name of a literal property with name {string} to {string}")
    public void theUserChangesTheNameOfALiteralPropertyWithNameTo(String literalPropertyName, String newName) {
       LiteralProperty literalProperty = (LiteralProperty) propertyRepository.getByName(literalPropertyName);
       controllerResponse = propertyController.updateProperty(literalProperty.getId(), newName);
    }

    @When("the user changes the name of a multiselect property with name {string} to {string}")
    public void theUserChangesTheNameOfAMultiselectPropertyWithNameTo(String mspropName, String newName) {
        MultiSelectProperty msprop = (MultiSelectProperty) propertyRepository.getByName(mspropName);
        controllerResponse = propertyController.updateProperty(msprop.getId(), newName);
    }

    @Then("a property does not exist error shall be thrown")
    public void aPropertyDoesNotExistErrorShallBeThrown() {
        assertEquals(HttpStatus.NOT_FOUND, controllerResponse.getStatusCode());
    }
}
