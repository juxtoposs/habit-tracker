package lt.viko.eif.habittracker.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HabitRequest validation constraints.
 */
class HabitRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidRequest_thenNoViolations() {
        HabitRequest request = new HabitRequest("Read a book", "Read 10 pages daily");
        Set<ConstraintViolation<HabitRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameIsBlank_thenValidationFails() {
        HabitRequest request = new HabitRequest("", "Description");
        Set<ConstraintViolation<HabitRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Habit name cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameExceedsLimit_thenValidationFails() {
        String longName = "A".repeat(101); // 101 символ
        HabitRequest request = new HabitRequest(longName, "Description");

        Set<ConstraintViolation<HabitRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}