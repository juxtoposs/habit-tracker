package lt.viko.eif.habittracker.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiError structure and factory methods.
 */
class ApiErrorTest {

    @Test
    void shouldCreateApiErrorWithoutFieldErrors() {
        ApiError error = ApiError.of(HttpStatus.NOT_FOUND, "Not Found Message", "/api/habits/1");

        assertNotNull(error.timestamp());
        assertEquals(404, error.status());
        assertEquals("Not Found", error.error());
        assertEquals("Not Found Message", error.message());
        assertEquals("/api/habits/1", error.path());
        assertTrue(error.fieldErrors().isEmpty());
    }

    @Test
    void shouldCreateApiErrorWithFieldErrors() {
        Map<String, String> fieldErrors = Map.of("name", "Name cannot be blank");
        ApiError error = ApiError.of(HttpStatus.BAD_REQUEST, "Validation failed", "/api/habits", fieldErrors);

        assertEquals(400, error.status());
        assertFalse(error.fieldErrors().isEmpty());
        assertEquals("Name cannot be blank", error.fieldErrors().get("name"));
    }
}