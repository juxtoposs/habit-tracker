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
import lt.viko.eif.habittracker.model.HabitLog;
import lt.viko.eif.habittracker.repository.HabitLogRepository;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.time.LocalDate;
import java.util.HashMap;

import java.time.LocalDate;
import java.util.HashMap;
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
    private final Map<String, Long> habitIds = new HashMap<>();
    private Long latestLogId;
    private String latestEtag;

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

        habitIds.clear();
        response = null;
        latestLogId = null;
        latestEtag = null;
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

            Habit savedHabit = habitRepository.save(habit);
            habitIds.put(savedHabit.getName(), savedHabit.getId());
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

    @Given("a habit named {string} exists with description {string}")
    public void aHabitNamedExistsWithDescription(String name, String description) {
        Habit habit = new Habit(name, description);
        Habit savedHabit = habitRepository.save(habit);
        habitIds.put(name, savedHabit.getId());
    }

    @Given("habit {string} was completed on {string}")
    public void habitWasCompletedOn(String habitName, String dateText) {
        Long habitId = habitIds.get(habitName);
        assertNotNull(habitId, "Habit was not found in scenario state: " + habitName);

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new AssertionError("Habit not found: " + habitName));

        HabitLog log = new HabitLog(LocalDate.parse(dateText));
        log.setHabit(habit);

        HabitLog savedLog = habitLogRepository.save(log);
        latestLogId = savedLog.getId();
    }

    @When("I send a POST request to complete habit {string} on {string}")
    public void iSendAPostRequestToCompleteHabitOn(String habitName, String dateText) {
        Long habitId = habitIds.get(habitName);
        assertNotNull(habitId, "Habit was not found in scenario state: " + habitName);

        String path = "/api/habits/" + habitId + "/logs?completedDate=" + dateText;
        response = restTemplate.postForEntity(path, null, String.class);
    }

    @When("I send a DELETE request for the latest log of {string} through habit {string}")
    public void iSendADeleteRequestForLatestLogThroughHabit(String originalHabitName, String throughHabitName) {
        Long throughHabitId = habitIds.get(throughHabitName);
        assertNotNull(throughHabitId, "Habit was not found in scenario state: " + throughHabitName);
        assertNotNull(latestLogId, "No latest log ID was stored");

        String path = "/api/habits/" + throughHabitId + "/logs/" + latestLogId;

        response = restTemplate.exchange(
                path,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );
    }

    @When("I send a GET request for logs of habit {string}")
    public void iSendAGetRequestForLogsOfHabit(String habitName) {
        Long habitId = habitIds.get(habitName);
        assertNotNull(habitId, "Habit was not found in scenario state: " + habitName);

        response = restTemplate.getForEntity(
                "/api/habits/" + habitId + "/logs",
                String.class
        );
    }

    @When("I send a GET request to {string} with If-None-Match from the previous response")
    public void iSendAGetRequestWithIfNoneMatchFromPreviousResponse(String path) {
        assertNotNull(latestEtag, "No ETag was stored from previous response");

        HttpHeaders headers = new HttpHeaders();
        headers.setIfNoneMatch(latestEtag);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        response = restTemplate.exchange(
                path,
                HttpMethod.GET,
                request,
                String.class
        );
    }

    @Then("the response should contain a validation error for field {string}")
    public void theResponseShouldContainAValidationErrorForField(String fieldName) throws Exception {
        JsonNode root = responseJson();
        JsonNode fieldErrors = root.path("fieldErrors");

        assertTrue(
                fieldErrors.has(fieldName),
                "Expected validation error for field: " + fieldName
        );
    }

    @Then("the response should contain completed date {string}")
    public void theResponseShouldContainCompletedDate(String dateText) throws Exception {
        JsonNode root = responseJson();

        assertTrue(
                containsFieldValue(root, "completedDate", dateText),
                "Expected response to contain completedDate: " + dateText
        );
    }

    @Then("the response header {string} should be present")
    public void theResponseHeaderShouldBePresent(String headerName) {
        assertNotNull(response, "No HTTP response was stored");

        String headerValue = response.getHeaders().getFirst(headerName);

        assertNotNull(headerValue, "Expected response header to be present: " + headerName);

        if ("ETag".equalsIgnoreCase(headerName)) {
            latestEtag = headerValue;
        }
    }

    @Then("the response header {string} should contain {string}")
    public void theResponseHeaderShouldContain(String headerName, String expectedText) {
        assertNotNull(response, "No HTTP response was stored");

        String headerValue = response.getHeaders().getFirst(headerName);

        assertNotNull(headerValue, "Expected response header to be present: " + headerName);
        assertTrue(
                headerValue.contains(expectedText),
                "Expected header " + headerName + " to contain: " + expectedText
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