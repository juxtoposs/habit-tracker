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
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    List<HabitLog> findByHabitId(Long habitId);

    /**
     * Marks a habit as completed on a specified date.
     *
     * @param habitId       habit ID
     * @param completedDate completion date
     * @return created log
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException  if habit is not found
     * @throws lt.viko.eif.habittracker.exception.DuplicateResourceException if log already exists for this date
     */
    HabitLog markCompleted(Long habitId, LocalDate completedDate);

    /**
     * Deletes a completion log only if it belongs to the selected habit.
     *
     * @param habitId habit identifier
     * @param logId log identifier
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if the habit or log is not found
     */
    void deleteLog(Long habitId, Long logId);

    /**
     * Finds a single completion log only if it belongs to the selected habit.
     *
     * @param habitId habit identifier
     * @param logId log identifier
     * @return habit completion log
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if the habit or log is not found
     */
    HabitLog findByIdAndHabitId(Long habitId, Long logId);
}
