package lt.viko.eif.habittracker.service;

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
        return habitLogRepository.findByHabitId(habitId);
    }

    @Override
    public HabitLog markCompleted(Long habitId, LocalDate completedDate) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit with ID " + habitId + " not found"));

        if (habitLogRepository.existsByHabitIdAndCompletedDate(habitId, completedDate)) {
            throw new IllegalArgumentException("Habit already marked for date " + completedDate);
        }

        HabitLog log = new HabitLog(completedDate);
        log.setHabit(habit);
        return habitLogRepository.save(log);
    }

    @Override
    public void deleteLog(Long logId) {
        habitLogRepository.deleteById(logId);
    }
}
