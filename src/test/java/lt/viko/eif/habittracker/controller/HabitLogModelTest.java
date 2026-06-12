package lt.viko.eif.habittracker.controller;

import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.model.HabitLog;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HabitLogModel mapping logic.
 */
class HabitLogModelTest {

    @Test
    void shouldMapHabitLogToModelWithHabitId() {
        // Given
        Habit habit = new Habit();
        habit.setId(42L);

        HabitLog log = new HabitLog(LocalDate.now());
        log.setId(1L);
        log.setHabit(habit);

        // When
        HabitLogModel model = new HabitLogModel(log);

        // Then
        assertEquals(1L, model.getId());
        assertEquals(LocalDate.now(), model.getCompletedDate());
        assertEquals(42L, model.getHabitId());
    }

    @Test
    void shouldMapHabitLogToModelWhenHabitIsNull() {
        // Given
        HabitLog log = new HabitLog(LocalDate.now());
        log.setId(1L);
        log.setHabit(null); // Habit відсутній

        // When
        HabitLogModel model = new HabitLogModel(log);

        // Then
        assertEquals(1L, model.getId());
        assertNull(model.getHabitId());
    }
}