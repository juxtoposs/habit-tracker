package lt.viko.eif.habittracker.service;

import lt.viko.eif.habittracker.exception.DuplicateResourceException;
import lt.viko.eif.habittracker.exception.ResourceNotFoundException;
import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.model.HabitLog;
import lt.viko.eif.habittracker.repository.HabitLogRepository;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of the habit log management service.
 */
@Service
@Transactional
public class HabitLogServiceImpl implements HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;

    /**
     * Constructor with dependency injection.
     *
     * @param habitLogRepository log repository
     * @param habitRepository    habit repository
     */
    public HabitLogServiceImpl(HabitLogRepository habitLogRepository, HabitRepository habitRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HabitLog> findByHabitId(Long habitId) {
        if (!habitRepository.existsById(habitId)) {
            throw new ResourceNotFoundException("Habit", habitId);
        }
        return habitLogRepository.findByHabitId(habitId);
    }

    @Override
    public HabitLog markCompleted(Long habitId, LocalDate completedDate) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit", habitId));

        if (habitLogRepository.existsByHabitIdAndCompletedDate(habitId, completedDate)) {
            throw new DuplicateResourceException("Habit already marked as completed for date " + completedDate);
        }

        HabitLog log = new HabitLog(completedDate);
        log.setHabit(habit);
        return habitLogRepository.save(log);
    }

    @Override
    public void deleteLog(Long habitId, Long logId) {
        if (!habitRepository.existsById(habitId)) {
            throw new ResourceNotFoundException("Habit", habitId);
        }

        HabitLog log = habitLogRepository.findByIdAndHabit_Id(logId, habitId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habit log with ID " + logId
                                + " was not found for habit with ID " + habitId
                ));
        habitLogRepository.delete(log);
    }
}
