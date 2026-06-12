package lt.viko.eif.habittracker.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lt.viko.eif.habittracker.dto.HabitRequest;
import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.repository.HabitLogRepository;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber step definitions for Habit API end-to-end tests.
 */
public class HabitStepDefinitions {

    private final TestRestTemplate restTemplate;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final ObjectMapper objectMapper;

    public HabitStepDefinitions(
            TestRestTemplate restTemplate,
            HabitRepository habitRepository,
            HabitLogRepository habitLogRepository,
            ObjectMapper objectMapper
    ) {
        this.restTemplate = restTemplate;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.objectMapper = objectMapper;
    }

    private ResponseEntity<String> response;

    /**
     * Clears the database before every Cucumber scenario.
     */
    @Before
    public void cleanDatabaseBeforeScenario() {
        habitLogRepository.deleteAll();
        habitRepository.deleteAll();
        response = null;
    }

    @Given("the habit repository is empty")
    public void theHabitRepositoryIsEmpty() {
        habitLogRepository.deleteAll();
        habitRepository.deleteAll();
    }

    @Given("the following habits exist:")
    public void theFollowingHabitsExist(DataTable dataTable) {
        habitLogRepository.deleteAll();
        habitRepository.deleteAll();

        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> columns : rows) {
            Habit habit = new Habit(
                    columns.get("name"),
                    columns.get("description")
            );

            habitRepository.save(habit);
        }
    }

    @When("I send a POST request to {string} with name {string} and description {string}")
    public void iSendAPostRequestWithNameAndDescription(
            String path,
            String name,
            String description
    ) {
        HabitRequest request = new HabitRequest(name, description);
        response = restTemplate.postForEntity(path, request, String.class);
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String path) {
        response = restTemplate.getForEntity(path, String.class);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        assertNotNull(response, "No HTTP response was stored");
        assertEquals(HttpStatus.valueOf(statusCode), response.getStatusCode());
    }

    @Then("the response should contain a habit named {string}")
    public void theResponseShouldContainAHabitNamed(String name) throws Exception {
        JsonNode root = responseJson();

        assertTrue(
                containsFieldValue(root, "name", name),
                "Expected response to contain a habit named: " + name
        );
    }

    @Then("the response should contain {int} habits")
    public void theResponseShouldContainHabits(int expectedCount) throws Exception {
        JsonNode root = responseJson();
        JsonNode habits = root.path("_embedded").path("habits");

        assertTrue(
                habits.isArray(),
                "Expected response to contain _embedded.habits array"
        );

        assertEquals(expectedCount, habits.size());
    }

    @Then("the response should contain a link named {string}")
    public void theResponseShouldContainALinkNamed(String rel) throws Exception {
        JsonNode root = responseJson();
        JsonNode links = root.path("_links");

        assertTrue(
                links.has(rel),
                "Expected response to contain HATEOAS link: " + rel
        );
    }

    private JsonNode responseJson() throws Exception {
        assertNotNull(response, "No HTTP response was stored");
        assertNotNull(response.getBody(), "Response body was empty");

        return objectMapper.readTree(response.getBody());
    }

    private boolean containsFieldValue(JsonNode node, String fieldName, String expectedValue) {
        if (node == null || node.isMissingNode()) {
            return false;
        }

        if (node.isObject()) {
            JsonNode value = node.get(fieldName);

            if (value != null && expectedValue.equals(value.asText())) {
                return true;
            }

            for (JsonNode child : node) {
                if (containsFieldValue(child, fieldName, expectedValue)) {
                    return true;
                }
            }
        }

        if (node.isArray()) {
            for (JsonNode child : node) {
                if (containsFieldValue(child, fieldName, expectedValue)) {
                    return true;
                }
            }
        }

        return false;
    }
}