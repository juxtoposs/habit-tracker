package lt.viko.eif.habittracker.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for ApiExceptionHandler (Global REST exception handler).
 */
class ApiExceptionHandlerTest {

    private final ApiExceptionHandler exceptionHandler = new ApiExceptionHandler();

    @Test
    void handleResourceNotFound_ShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Habit", 1L);
        HttpServletRequest request = new MockHttpServletRequest("GET", "/api/habits/1");

        ResponseEntity<ApiError> response = exceptionHandler.handleResourceNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Habit with ID 1 was not found", response.getBody().message());
        assertEquals(404, response.getBody().status());
    }

    @Test
    void handleDuplicateResource_ShouldReturn409() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate log");
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/habits/1/logs");

        ResponseEntity<ApiError> response = exceptionHandler.handleDuplicateResource(ex, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Duplicate log", response.getBody().message());
        assertEquals(409, response.getBody().status());
    }

    @Test
    void handleMissingParameter_ShouldReturn400() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("completedDate", "String");
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/habits/1/logs");

        ResponseEntity<ApiError> response = exceptionHandler.handleMissingParameter(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Missing required parameter: completedDate", response.getBody().message());
    }

    @Test
    void handleConstraintViolation_ShouldReturn400() {
        ConstraintViolationException ex = new ConstraintViolationException("Name cannot be blank", Collections.emptySet());
        HttpServletRequest request = new MockHttpServletRequest("POST", "/api/habits");

        ResponseEntity<ApiError> response = exceptionHandler.handleConstraintViolation(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Name cannot be blank", response.getBody().message());
    }
}