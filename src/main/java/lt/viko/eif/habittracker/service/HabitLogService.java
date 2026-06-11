package lt.viko.eif.habittracker.service;

import lt.viko.eif.habittracker.model.HabitLog;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing habit completion logs.
 * Defines business logic for marking completions following DIP (SOLID).
 */
public interface HabitLogService {

    /**
     * Returns all completion logs for a specified habit.
     *
     * @param habitId habit ID
     * @return list of logs
     */
    List<HabitLog> findByHabitId(Long habitId);

    /**
     * Marks a habit as completed on a specified date.
     *
     * @param habitId       habit ID
     * @param completedDate completion date
     * @return created log
     * @throws IllegalArgumentException if habit is not found or log already exists
     */
    HabitLog markCompleted(Long habitId, LocalDate completedDate);

    /**
     * Deletes a completion log by ID.
     *
     * @param logId log ID
     */
    void deleteLog(Long logId);
}
