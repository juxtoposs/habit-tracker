package lt.viko.eif.habittracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HabitEntityTest {

    @Test
    void prePersist_ShouldSetCreatedAtIfNull() {
        Habit habit = new Habit();
        habit.setName("Test Habit");
        habit.setDescription("Testing PrePersist");

        assertNull(habit.getCreatedAt());

        // Trigger the JPA lifecycle hook manually
        habit.onCreate();

        assertNotNull(habit.getCreatedAt(), "CreatedAt should be initialized by @PrePersist");
    }

    @Test
    void prePersist_ShouldNotOverwriteExistingCreatedAt() {
        Habit habit = new Habit("Test Habit", "Desc");
        // Constructor initializes createdAt
        var originalTime = habit.getCreatedAt();

        habit.onCreate();

        assertEquals(originalTime, habit.getCreatedAt(), "CreatedAt should not be overwritten if already set");
    }
}