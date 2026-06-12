package lt.viko.eif.habittracker.cucumber;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cucumber step definitions mapping Gherkin steps to automated integration test actions.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HabitStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private HabitRepository habitRepository;

    private ResponseEntity<String> response;

    @Given("the habit repository is empty")
    public void theHabitRepositoryIsEmpty() {
        habitRepository.deleteAll();
    }

    @Given("the following habits exist:")
    public void theFollowingHabitsExist(DataTable dataTable) {
        habitRepository.deleteAll();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> columns : rows) {
            habitRepository.save(new Habit(columns.get("name"), columns.get("description")));
        }
    }

    @When("I send a POST request to {string} with name {string} and description {string}")
    public void iSendAPOSTRequestToWithNameAndDescription(String path, String name, String description) {
        Habit habit = new Habit(name, description);
        response = restTemplate.postForEntity(path, habit, String.class);
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String path) {
        response = restTemplate.getForEntity(path, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @Then("the response should contain a habit named {string}")
    public void theResponseShouldContainAHabitNamed(String name) {
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(name));
    }

    @Then("the response should contain {int} habits")
    public void theResponseShouldContainHabits(int expectedCount) {
        assertNotNull(response.getBody());
        assertEquals(expectedCount, habitRepository.count());
    }
}