package lt.viko.eif.habittracker.service;

import lt.viko.eif.habittracker.model.Habit;
import lt.viko.eif.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the habit management service.
 * Contains business logic and delegates data access to the repository.
 */
@Service
@Transactional
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;

    /**
     * Constructor with dependency injection (DIP).
     *
     * @param habitRepository habit repository
     */
    public HabitServiceImpl(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Habit> findAll() {
        return habitRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Habit> findById(Long id) {
        return habitRepository.findById(id);
    }

    @Override
    public Habit create(Habit habit) {
        return habitRepository.save(habit);
    }

    @Override
    public Habit update(Long id, Habit habitDetails) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit with ID " + id + " not found"));

        habit.setName(habitDetails.getName());
        habit.setDescription(habitDetails.getDescription());
        return habitRepository.save(habit);
    }

    @Override
    public void delete(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new IllegalArgumentException("Habit with ID " + id + " not found");
        }
        habitRepository.deleteById(id);
    }
}
