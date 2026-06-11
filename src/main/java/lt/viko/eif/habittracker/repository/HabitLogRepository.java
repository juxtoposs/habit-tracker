package lt.viko.eif.habittracker.repository;

import lt.viko.eif.habittracker.model.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for accessing HabitLog data.
 * Provides standard CRUD operations and search by date.
 */
@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    /**
     * Finds all completion logs for a specific habit.
     *
     * @param habitId habit ID
     * @return list of logs
     */
    List<HabitLog> findByHabitId(Long habitId);

    /**
     * Checks if a log exists for a habit on a given date.
     *
     * @param habitId       habit ID
     * @param completedDate completion date
     * @return true if log exists
     */
    boolean existsByHabitIdAndCompletedDate(Long habitId, LocalDate completedDate);
}
