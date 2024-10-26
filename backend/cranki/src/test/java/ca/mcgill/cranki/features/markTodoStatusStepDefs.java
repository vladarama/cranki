package ca.mcgill.cranki.features;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class markTodoStatusStepDefs {
    @Given("I have the following todos in my todo list:")
    public void iHaveTheFollowingTodosInMyTodoList(DataTable existingItems) {
        List<Map<String, String>> rows = dataTable.asMaps();
        for (var row: rows) {

        }
    }

    @When("I mark {string} as done")
    public void iMarkAsDone(String arg0) {
    }

    @Then("the status of {string} should be {string}")
    public void theStatusOfShouldBe(String arg0, String arg1) {
    }

    @And("the tatus of {string} should be {string}")
    public void theTatusOfShouldBe(String arg0, String arg1) {
    }

    @When("I mark {string} as in progress")
    public void iMarkAsInProgress(String arg0) {
    }

    @Then("the status of {string} should remain {string}")
    public void theStatusOfShouldRemain(String arg0, String arg1) {
    }

    @And("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String arg0) {
    }
}
