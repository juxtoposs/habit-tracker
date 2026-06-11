package lt.viko.eif.habittracker.service;

import lt.viko.eif.habittracker.model.Habit;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing habits.
 * Defines business logic for habit operations following DIP (SOLID).
 */
public interface HabitService {

    /**
     * Returns a list of all habits.
     *
     * @return list of habits
     */
    List<Habit> findAll();

    /**
     * Finds a habit by ID.
     *
     * @param id habit identifier
     * @return Optional containing the habit or empty
     */
    Optional<Habit> findById(Long id);

    /**
     * Creates a new habit.
     *
     * @param habit habit object to create
     * @return created habit with assigned ID
     */
    Habit create(Habit habit);

    /**
     * Updates an existing habit.
     *
     * @param id    habit identifier
     * @param habit new habit data
     * @return updated habit
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    Habit update(Long id, Habit habit);

    /**
     * Deletes a habit by ID.
     *
     * @param id habit identifier
     * @throws lt.viko.eif.habittracker.exception.ResourceNotFoundException if habit is not found
     */
    void delete(Long id);
}
